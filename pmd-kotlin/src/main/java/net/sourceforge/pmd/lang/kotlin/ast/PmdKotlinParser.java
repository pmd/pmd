/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseParser;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.kotlin.KotlinLanguageProperties;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;

/**
 * Adapter for the KotlinParser.
 *
 * <p>Each parse injects a fresh {@link InterruptibleParserATNSimulator} with its own DFA arrays
 * and {@link PredictionContextCache}. Using per-parse instances avoids lock contention when
 * PMD parses files in parallel, which would otherwise cause severe performance degradation due
 * to the complexity of the Kotlin grammar.
 *
 * <p>A per-file parse timeout acts as a safety net. Files exceeding the timeout are skipped with a warning.
 * The timeout is configured via {@link net.sourceforge.pmd.lang.kotlin.KotlinLanguageProperties#PARSE_TIMEOUT_SECONDS}.
 *
 * <p>Error handling strategy:
 * <ul>
 *   <li><b>Lexer errors</b> (e.g. unrecognized tokens) and <b>Parser errors</b> (e.g. unexpected token structure)
 *       are collected and lexing/parsing continues.</li>
 *   <li>Further lexer/parser errors are collected as suppressed exceptions.</li>
 *   <li>The first occurred lexer/parser exception is thrown at the end and no rules are executed.</li>
 * </ul>
 *
 * <p>PMD reports the file as a {@code ProcessingError} and skips rule analysis for it.
 * All other files continue to be processed. Exit code 5 is returned by the CLI if
 * any processing errors occurred (suppressible with {@code --no-fail-on-error}).
 */
public final class PmdKotlinParser extends AntlrBaseParser<KotlinNode, KtKotlinFile> {

    private static final Logger LOG = LoggerFactory.getLogger(PmdKotlinParser.class);

    // TODO: introduce KotlinLanguageProcessor extends BatchLanguageProcessor<KotlinLanguageProperties>
    //       to own the executor (proper shutdown via AutoCloseable) and supply timeout via
    //       task.getLanguageProcessor(), removing the need for constructor injection.
    private static final ExecutorService PARSE_EXECUTOR =
            Executors.newCachedThreadPool(r -> {
                Thread t = new Thread(r, "kotlin-parser");
                t.setDaemon(true);
                return t;
            });

    private final int timeoutSeconds;

    public PmdKotlinParser() {
        this(KotlinLanguageProperties.PARSE_TIMEOUT_SECONDS.defaultValue());
    }

    public PmdKotlinParser(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    protected KtKotlinFile parse(final Lexer lexer, ParserTask task) {
        AntlrErrorListener errorListener = new AntlrErrorListener(task);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener.lexerErrorListener());

        KotlinParser parser = new KotlinParser(new CommonTokenStream(lexer));
        parser.setInterpreter(freshSimulator(parser));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener.parserErrorListener());

        String fileName = task.getFileId().getFileName();
        LOG.debug("Parsing Kotlin file {}", fileName);

        Future<KtKotlinFile> future = PARSE_EXECUTOR.submit(
                () -> parser.kotlinFile().makeAstInfo(task));

        try {
            KtKotlinFile ktKotlinFile = future.get(timeoutSeconds, TimeUnit.SECONDS);
            if (errorListener.hasErrors()) {
                throw errorListener.getException();
            }
            return ktKotlinFile;
        } catch (TimeoutException e) {
            future.cancel(true);
            LOG.warn("Kotlin parse timeout ({}s) exceeded for file: {}. Skipping.", timeoutSeconds, fileName);
            throw new ParseException("Parse timeout (" + timeoutSeconds + "s) exceeded for " + fileName);
        } catch (ExecutionException e) {
            return unwrapExecutionException(e, fileName);
        } catch (InterruptedException | CancellationException e) {
            Thread.currentThread().interrupt();
            throw new ParseException("Parse interrupted for " + fileName);
        }
    }

    private static InterruptibleParserATNSimulator freshSimulator(KotlinParser parser) {
        DFA[] decisionToDfa = new DFA[KotlinParser._ATN.getNumberOfDecisions()];
        for (int i = 0; i < decisionToDfa.length; i++) {
            decisionToDfa[i] = new DFA(KotlinParser._ATN.getDecisionState(i), i);
        }
        return new InterruptibleParserATNSimulator(
                parser, KotlinParser._ATN, decisionToDfa, new PredictionContextCache());
    }

    private static KtKotlinFile unwrapExecutionException(ExecutionException e, String fileName) {
        Throwable cause = e.getCause();
        if (cause instanceof ParseException) {
            throw (ParseException) cause;
        }
        if (cause instanceof InterruptibleParserATNSimulator.ParseCancelledException) {
            throw new ParseException("Parse cancelled (interrupted) for " + fileName);
        }
        throw new ParseException(cause);
    }

    private static final class AntlrErrorListener {
        private final ParserTask task;
        private FileAnalysisException exception;

        private AntlrErrorListener(ParserTask task) {
            this.task = task;
        }

        private void addException(FileAnalysisException exception) {
            if (this.exception == null) {
                this.exception = exception;
            } else {
                this.exception.addSuppressed(exception);
            }
        }

        boolean hasErrors() {
            return exception != null;
        }

        FileAnalysisException getException() {
            return exception;
        }

        ANTLRErrorListener lexerErrorListener() {
            return new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                        int line, int charPositionInLine,
                                        String msg, RecognitionException e) {
                    addException(new LexException(line, charPositionInLine, task.getFileId(), msg, null));
                }
            };
        }

        ANTLRErrorListener parserErrorListener() {
            return new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                        int line, int charPositionInLine,
                                        String msg, RecognitionException e) {
                    addException(new ParseException(msg)
                            .withLocation(FileLocation.caret(task.getFileId(), line, charPositionInLine)));
                }
            };
        }
    }

    @Override
    protected Lexer getLexer(final CharStream source) {
        return new KotlinLexer(source);
    }
}
