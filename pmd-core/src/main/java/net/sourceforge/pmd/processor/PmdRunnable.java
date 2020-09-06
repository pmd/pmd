/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.io.File;
import java.io.IOException;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.internal.RulesetStageDependencyHelper;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * A processing task for a single file.
 */
abstract class PmdRunnable implements Runnable {

    private final DataSource dataSource;
    private final File file;
    private final GlobalAnalysisListener ruleContext;

    private final PMDConfiguration configuration;

    private final RulesetStageDependencyHelper dependencyHelper;

    PmdRunnable(DataSource dataSource,
                GlobalAnalysisListener ruleContext,
                PMDConfiguration configuration) {
        this.dataSource = dataSource;
        // this is the real, canonical and absolute filename (not shortened)
        String realFileName = dataSource.getNiceFileName(false, null);

        this.file = new File(realFileName);
        this.ruleContext = ruleContext;
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

        try (FileAnalysisListener listener = ruleContext.startFileAnalysis(dataSource)) {

            LanguageVersion langVersion = configuration.getLanguageVersionOfFile(file.getPath());


            // Coarse check to see if any RuleSet applies to file, will need to do a finer RuleSet specific check later
            if (ruleSets.applies(file)) {
                if (configuration.getAnalysisCache().isUpToDate(file)) {
                    reportCachedRuleViolations(listener, file);
                } else {
                    try {
                        processSource(listener, langVersion, ruleSets);
                    } catch (Exception e) {
                        configuration.getAnalysisCache().analysisFailed(file);

                        // The listener handles logging if needed,
                        // it may also rethrow the error, as a FileAnalysisException (which we let through below)
                        listener.onError(new Report.ProcessingError(e, file.getPath()));
                    }
                }
            }
        } catch (FileAnalysisException e) {
            throw e; // bubble managed exceptions, they were already reported
        } catch (Exception e) {
            throw FileAnalysisException.wrap(file.getPath(), "Exception while closing listener", e);
        }

        TimeTracker.finishThread();
    }

    private void processSource(FileAnalysisListener listener, LanguageVersion languageVersion, RuleSets ruleSets) throws IOException, FileAnalysisException {
        String fullSource = DataSource.readToString(dataSource, configuration.getSourceEncoding());
        String filename = dataSource.getNiceFileName(false, null);

        processSource(fullSource, ruleSets, listener, languageVersion, filename);
    }


    private void reportCachedRuleViolations(final FileAnalysisListener ctx, File file) {
        for (final RuleViolation rv : configuration.getAnalysisCache().getCachedViolations(file)) {
            ctx.onRuleViolation(rv);
        }
    }

    private RootNode parse(Parser parser, ParserTask task) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.PARSER)) {
            return parser.parse(task);
        }
    }


    private void processSource(String sourceCode,
                               RuleSets ruleSets,
                               FileAnalysisListener listener,
                               LanguageVersion languageVersion,
                               String filename) throws FileAnalysisException {

        ParserTask task = new ParserTask(
            languageVersion,
            filename,
            sourceCode,
            SemanticErrorReporter.noop(), // TODO
            configuration.getSuppressMarker()
        );

        Parser parser = languageVersion.getLanguageVersionHandler().getParser();

        RootNode rootNode = parse(parser, task);

        dependencyHelper.runLanguageSpecificStages(ruleSets, languageVersion, rootNode);

        ruleSets.apply(rootNode, listener);
    }

}
