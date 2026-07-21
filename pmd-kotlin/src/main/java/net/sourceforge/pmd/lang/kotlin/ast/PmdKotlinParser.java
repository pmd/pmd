/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseParserWithErrorHandling;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.kotlin.KotlinHandler;
import net.sourceforge.pmd.lang.kotlin.KotlinLanguageModule;
import net.sourceforge.pmd.lang.kotlin.KotlinLanguageProcessor;
import net.sourceforge.pmd.lang.kotlin.KotlinLanguageProperties;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;

/**
 * Adapter for the KotlinParser.
 *
 * <p>Each parse injects a fresh {@link InterruptibleParserATNSimulator} with its own DFA arrays
 * and {@link PredictionContextCache}. Using per-parse instances avoids lock contention when
 * PMD parses files in parallel and prevents unbounded ATN state accumulation across files,
 * which would otherwise cause severe slowdowns with the complexity of the Kotlin grammar.
 *
 * <p>A per-file parse timeout acts as a safety net. Files exceeding the timeout are skipped with a processing
 * error. The timeout is configured via {@link KotlinLanguageProperties#PARSE_TIMEOUT_SECONDS}.
 *
 * <p>Error handling strategy, see {@link AntlrBaseParserWithErrorHandling}</p>
 */
public final class PmdKotlinParser extends AntlrBaseParserWithErrorHandling<KotlinNode, KtKotlinFile, KotlinParser, KotlinLexer> {

    private static final Logger LOG = LoggerFactory.getLogger(PmdKotlinParser.class);

    private final ExecutorService timeoutExecutor;

    /**
     * @deprecated Since 7.25.0. Don't create a parser directly. Use {@link KotlinLanguageModule#getInstance()},
     *             {@link KotlinLanguageModule#createProcessor(LanguagePropertyBundle)},
     *             {@link KotlinLanguageProcessor#services()}, {@link KotlinHandler#getParser()} instead.
     */
    @Deprecated
    public PmdKotlinParser() {
        this.timeoutExecutor = null;
    }

    PmdKotlinParser(ExecutorService timeoutExecutor) {
        this.timeoutExecutor = timeoutExecutor;
    }

    @Override
    protected KtKotlinFile parse(final KotlinParser kotlinParser, ParserTask task) {
        kotlinParser.setInterpreter(freshSimulator(kotlinParser));

        FileId fileId = task.getFileId();
        String fileName = fileId.getOriginalPath();
        // Note: KotlinLanguageProcessor will be closed by LanguageProcessorRegistry as part of PmdAnalysis
        KotlinLanguageProcessor processor = (KotlinLanguageProcessor) task.getLanguageProcessor(); //NOPMD: CloseResource
        int timeoutSeconds = processor.getProperties().getParseTimeoutSeconds();

        LOG.debug("Parsing Kotlin file {} (timeout: {}s)", fileName, timeoutSeconds);

        Callable<KtKotlinFile> callable = () -> kotlinParser.kotlinFile().makeAstInfo(task);
        final Future<KtKotlinFile> future;
        if (timeoutExecutor != null) {
            future = timeoutExecutor.submit(callable);
        } else {
            LOG.warn("Incorrect usage of PmdKotlinParser! Not using timeoutExecutor, not applying timeout!");
            try {
                future = CompletableFuture.completedFuture(callable.call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        try {
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            LOG.warn("Kotlin parse timeout ({}s) exceeded for file: {}. Skipping.", timeoutSeconds, fileName);
            ParseException parseException = new ParseException("Parse timeout (" + timeoutSeconds + "s) exceeded");
            parseException.setFileId(fileId);
            throw parseException;
        } catch (ExecutionException e) {
            return unwrapExecutionException(e, fileId);
        } catch (InterruptedException | CancellationException e) {
            Thread.currentThread().interrupt();
            ParseException parseException = new ParseException("Parse interrupted");
            parseException.setFileId(fileId);
            throw parseException;
        }
    }

    // Note: Not using the static KotlinParser._decisionToDFA and KotlinParser._sharedContextCache
    // as this turned out to introduce thread contention. Needed unshared DFA and contextCache per
    // file/simulator to prevent this.
    private static InterruptibleParserATNSimulator freshSimulator(KotlinParser parser) {
        DFA[] decisionToDfa = new DFA[KotlinParser._ATN.getNumberOfDecisions()];
        for (int i = 0; i < decisionToDfa.length; i++) {
            decisionToDfa[i] = new DFA(KotlinParser._ATN.getDecisionState(i), i);
        }
        return new InterruptibleParserATNSimulator(
                parser, KotlinParser._ATN, decisionToDfa, new PredictionContextCache());
    }

    private static KtKotlinFile unwrapExecutionException(ExecutionException e, FileId fileId) {
        Throwable cause = e.getCause();
        if (cause instanceof ParseException) {
            throw (ParseException) cause;
        }
        if (cause instanceof InterruptibleParserATNSimulator.ParseCancelledException) {
            ParseException parseException = new ParseException("Parse cancelled (interrupted)");
            parseException.setFileId(fileId);
            throw parseException;
        }
        ParseException parseException = new ParseException(cause);
        parseException.setFileId(fileId);
        throw parseException;
    }

    @Override
    protected KotlinLexer getLexer(final CharStream source) {
        return new KotlinLexer(source);
    }

    @Override
    protected KotlinParser getParser(KotlinLexer lexer) {
        return new KotlinParser(new CommonTokenStream(lexer));
    }
}
