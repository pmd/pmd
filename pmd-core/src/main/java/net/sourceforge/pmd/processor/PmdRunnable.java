/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.cache.AnalysisCache;
import net.sourceforge.pmd.internal.RulesetStageDependencyHelper;
import net.sourceforge.pmd.internal.SystemProps;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.document.TextDocument;
import net.sourceforge.pmd.util.document.TextFile;

/**
 * A processing task for a single file.
 */
abstract class PmdRunnable implements Runnable {

    private final TextFile textFile;
    private final GlobalAnalysisListener ruleContext;

    private final AnalysisCache analysisCache;
    /** @deprecated Get rid of this */
    @Deprecated
    private final PMDConfiguration configuration;

    private final RulesetStageDependencyHelper dependencyHelper;

    PmdRunnable(TextFile textFile,
                GlobalAnalysisListener ruleContext,
                PMDConfiguration configuration) {
        this.textFile = textFile;
        this.ruleContext = ruleContext;
        this.analysisCache = configuration.getAnalysisCache();
        this.configuration = configuration;
        this.dependencyHelper = new RulesetStageDependencyHelper(configuration);
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

        RuleSets ruleSets = getRulesets();

        try (FileAnalysisListener listener = ruleContext.startFileAnalysis(textFile)) {

            // Coarse check to see if any RuleSet applies to file, will need to do a finer RuleSet specific check later
            if (ruleSets.applies(textFile)) {
                try (TextDocument textDocument = TextDocument.create(textFile)) {

                    if (analysisCache.isUpToDate(textDocument)) {
                        reportCachedRuleViolations(listener, textDocument);
                    } else {
                        try {
                            processSource(listener, textDocument, ruleSets);
                        } catch (Exception | StackOverflowError | AssertionError e) {
                            if (e instanceof Error && !SystemProps.isErrorRecoveryMode()) { // NOPMD:
                                throw e;
                            }
                            analysisCache.analysisFailed(textDocument);

                            // The listener handles logging if needed,
                            // it may also rethrow the error, as a FileAnalysisException (which we let through below)
                            listener.onError(new Report.ProcessingError(e, textFile.getDisplayName()));
                        }
                    }
                }
            }
        } catch (FileAnalysisException e) {
            throw e; // bubble managed exceptions, they were already reported
        } catch (Exception e) {
            throw FileAnalysisException.wrap(textFile.getDisplayName(), "An unknown exception occurred", e);
        }

        TimeTracker.finishThread();
    }

    private void reportCachedRuleViolations(final FileAnalysisListener ctx, TextDocument file) {
        for (final RuleViolation rv : analysisCache.getCachedViolations(file)) {
            ctx.onRuleViolation(rv);
        }
    }

    private RootNode parse(Parser parser, ParserTask task) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.PARSER)) {
            return parser.parse(task);
        }
    }


    private void processSource(FileAnalysisListener listener,
                               TextDocument textDocument,
                               RuleSets ruleSets) throws FileAnalysisException {

        ParserTask task = new ParserTask(
            textDocument,
            SemanticErrorReporter.noop() // TODO
        );


        LanguageVersionHandler handler = textDocument.getLanguageVersion().getLanguageVersionHandler();

        handler.declareParserTaskProperties(task.getProperties());
        task.getProperties().setProperty(ParserTask.COMMENT_MARKER, configuration.getSuppressMarker());

        Parser parser = handler.getParser();

        RootNode rootNode = parse(parser, task);

        dependencyHelper.runLanguageSpecificStages(ruleSets, textDocument.getLanguageVersion(), rootNode);

        ruleSets.apply(rootNode, listener);
    }

}
