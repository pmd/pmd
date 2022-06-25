/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.internal.SystemProps;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.ast.SemanticException;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * A processing task for a single file.
 */
abstract class PmdRunnable implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(PmdRunnable.class);
    private final DataSource dataSource;
    private final File file;
    private final GlobalAnalysisListener globalListener;

    private final PMDConfiguration configuration;

    PmdRunnable(DataSource dataSource,
                GlobalAnalysisListener globalListener,
                PMDConfiguration configuration) {
        this.dataSource = dataSource;
        // this is the real, canonical and absolute filename (not shortened)
        String realFileName = dataSource.getNiceFileName(false, null);

        this.file = new File(realFileName);
        this.globalListener = globalListener;
        this.configuration = configuration;
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

        try (FileAnalysisListener listener = globalListener.startFileAnalysis(dataSource)) {

            LanguageVersion langVersion = configuration.getLanguageVersionOfFile(file.getPath());


            // Coarse check to see if any RuleSet applies to file, will need to do a finer RuleSet specific check later
            if (ruleSets.applies(file)) {
                if (configuration.getAnalysisCache().isUpToDate(file)) {
                    LOG.trace("Skipping file (lang: {}) because it was found in the cache: {}", langVersion, dataSource.getNiceFileName(false, null));
                    reportCachedRuleViolations(listener, file);
                } else {
                    LOG.trace("Processing file (lang: {}): {}", langVersion, dataSource.getNiceFileName(false, null));
                    try {
                        processSource(listener, langVersion, ruleSets);
                    } catch (Exception | StackOverflowError | AssertionError e) {
                        if (e instanceof Error && !SystemProps.isErrorRecoveryMode()) { // NOPMD:
                            throw e;
                        }

                        // The listener handles logging if needed,
                        // it may also rethrow the error, as a FileAnalysisException (which we let through below)
                        listener.onError(new Report.ProcessingError(e, file.getPath()));
                    }
                }
            } else {
                LOG.trace("Skipping file (lang: {}) because no rule applies: {}", langVersion, dataSource.getNiceFileName(false, null));
            }
        } catch (FileAnalysisException e) {
            throw e; // bubble managed exceptions, they were already reported
        } catch (Exception e) {
            throw FileAnalysisException.wrap(file.getPath(), "An unknown exception occurred", e);
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
        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.PARSER)) {
            return parser.parse(task);
        }
    }


    private void processSource(String sourceCode,
                               RuleSets ruleSets,
                               FileAnalysisListener listener,
                               LanguageVersion languageVersion,
                               String filename) throws FileAnalysisException {

        SemanticErrorReporter reporter = SemanticErrorReporter.reportToLogger(configuration.getReporter());
        ParserTask task = new ParserTask(
            languageVersion,
            filename,
            sourceCode,
            reporter,
            configuration.getClassLoader()
        );


        LanguageVersionHandler handler = languageVersion.getLanguageVersionHandler();

        handler.declareParserTaskProperties(task.getProperties());
        task.getProperties().setProperty(ParserTask.COMMENT_MARKER, configuration.getSuppressMarker());

        Parser parser = handler.getParser();

        RootNode rootNode = parse(parser, task);

        SemanticException semanticError = reporter.getFirstError();
        if (semanticError != null) {
            // cause a processing error to be reported and rule analysis to be skipped
            throw semanticError;
        }

        ruleSets.apply(rootNode, listener);
    }

}
