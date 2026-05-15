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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseParser;
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
 */
public final class PmdKotlinParser extends AntlrBaseParser<KotlinNode, KtKotlinFile> {

    private static final Logger LOG = LoggerFactory.getLogger(PmdKotlinParser.class);

    private static final ExecutorService PARSE_EXECUTOR =
            Executors.newCachedThreadPool(r -> {
                Thread t = new Thread(r, "kotlin-parser");
                t.setDaemon(true);
                return t;
            });

    private final int timeoutSeconds;

    public PmdKotlinParser(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    protected KtKotlinFile parse(final Lexer lexer, ParserTask task) {
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        KotlinParser parser = new KotlinParser(tokens);
        parser.setInterpreter(freshSimulator(parser));

        String fileName = task.getFileId().getFileName();
        Future<KtKotlinFile> future = PARSE_EXECUTOR.submit(
                () -> parser.kotlinFile().makeAstInfo(task));

        try {
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
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

    @Override
    protected Lexer getLexer(final CharStream source) {
        return new KotlinLexer(source);
    }
}
