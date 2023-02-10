/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.cache.AnalysisCache;
import net.sourceforge.pmd.internal.SystemProps;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageProcessor.AnalysisTask;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.ast.SemanticException;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.reporting.FileAnalysisListener;

/**
 * A processing task for a single file.
 */
abstract class PmdRunnable implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(PmdRunnable.class);
    private final TextFile textFile;
    private final AnalysisTask task;

    PmdRunnable(TextFile textFile, AnalysisTask task) {
        this.textFile = textFile;
        this.task = task;
    }

    /**
     * This is only called within the run method (when we are on the actual carrier thread).
     * That way an implementation that uses a ThreadLocal will see the
     * correct thread.
     */
    protected abstract RuleSets getRulesets();

    @Override
    public void run() throws FileAnalysisException {
        TimeTracker.initThread();

        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.FILE_PROCESSING);
             FileAnalysisListener listener = task.getListener().startFileAnalysis(textFile)) {

            RuleSets ruleSets = getRulesets();

            // Coarse check to see if any RuleSet applies to file, will need to do a finer RuleSet specific check later
            if (ruleSets.applies(textFile)) {
                AnalysisCache analysisCache = task.getAnalysisCache();
                try (TextDocument textDocument = TextDocument.create(textFile);
                     FileAnalysisListener cacheListener = analysisCache.startFileAnalysis(textDocument)) {

                    @SuppressWarnings("PMD.CloseResource")
                    FileAnalysisListener completeListener = FileAnalysisListener.tee(listOf(listener, cacheListener));

                    if (analysisCache.isUpToDate(textDocument)) {
                        LOG.trace("Skipping file (lang: {}) because it was found in the cache: {}", textFile.getLanguageVersion(), textFile.getPathId());
                        // note: no cache listener here
                        //                         vvvvvvvv
                        reportCachedRuleViolations(listener, textDocument);
                    } else {
                        LOG.trace("Processing file (lang: {}): {}", textFile.getLanguageVersion(), textFile.getPathId());
                        try {
                            processSource(completeListener, textDocument, ruleSets);
                        } catch (Exception | StackOverflowError | AssertionError e) {
                            if (e instanceof Error && !SystemProps.isErrorRecoveryMode()) { // NOPMD:
                                throw e;
                            }

                            // The listener handles logging if needed,
                            // it may also rethrow the error, as a FileAnalysisException (which we let through below)
                            completeListener.onError(new Report.ProcessingError(e, textFile.getDisplayName()));
                        }
                    }
                }
            } else {
                LOG.trace("Skipping file (lang: {}) because no rule applies: {}", textFile.getLanguageVersion(), textFile.getPathId());
            }
        } catch (FileAnalysisException e) {
            throw e; // bubble managed exceptions, they were already reported
        } catch (Exception e) {
            throw FileAnalysisException.wrap(textFile.getDisplayName(), "An unknown exception occurred", e);
        }

        TimeTracker.finishThread();
    }

    private void reportCachedRuleViolations(final FileAnalysisListener ctx, TextDocument file) {
        for (final RuleViolation rv : task.getAnalysisCache().getCachedViolations(file)) {
            ctx.onRuleViolation(rv);
        }
    }

    private RootNode parse(Parser parser, ParserTask task) {
        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.PARSER)) {
            return parser.parse(task);
        }
    }


    private void processSource(FileAnalysisListener listener,
                               TextDocument textDocument,
                               RuleSets ruleSets) throws FileAnalysisException {

        SemanticErrorReporter reporter = SemanticErrorReporter.reportToLogger(task.getMessageReporter());
        @SuppressWarnings("PMD.CloseResource")
        LanguageProcessor processor = task.getLpRegistry().getProcessor(textDocument.getLanguageVersion().getLanguage());
        ParserTask parserTask = new ParserTask(textDocument,
                                               reporter,
                                               task.getLpRegistry());

        LanguageVersionHandler handler = processor.services();

        Parser parser = handler.getParser();

        RootNode rootNode = parse(parser, parserTask);

        SemanticException semanticError = reporter.getFirstError();
        if (semanticError != null) {
            // cause a processing error to be reported and rule analysis to be skipped
            throw semanticError;
        }

        ruleSets.apply(rootNode, listener);
    }

}
