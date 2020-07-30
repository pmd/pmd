/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
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
import net.sourceforge.pmd.util.datasource.DataSource;

class PmdRunnable implements Runnable {

    private static final Logger LOG = Logger.getLogger(PmdRunnable.class.getName());

    private final DataSource dataSource;
    private final File file;
    private final GlobalAnalysisListener ruleContext;

    // this should only be used within the run method (when we are on the actual carrier thread)
    private final ThreadLocal<RuleSets> ruleSetCopy;

    // rename so that it's significantly different from tc.rulesets
    private final Supplier<RuleSets> ruleSetMaker;
    private final PMDConfiguration configuration;

    private final RulesetStageDependencyHelper dependencyHelper;

    PmdRunnable(DataSource dataSource,
                GlobalAnalysisListener ruleContext,
                ThreadLocal<RuleSets> ruleSetCopy,
                RuleSets ruleSets,
                PMDConfiguration configuration) {
        this.ruleSetCopy = ruleSetCopy;
        this.ruleSetMaker = () -> new RuleSets(ruleSets);
        this.dataSource = dataSource;
        // this is the real, canonical and absolute filename (not shortened)
        String realFileName = dataSource.getNiceFileName(false, null);

        this.file = new File(realFileName);
        this.ruleContext = ruleContext;
        this.configuration = configuration;
        this.dependencyHelper = new RulesetStageDependencyHelper(configuration);
    }

    @Override
    public void run() {
        TimeTracker.initThread();

        RuleSets ruleSets = ruleSetCopy.get();
        if (ruleSets == null) {
            ruleSets = ruleSetMaker.get();
            ruleSetCopy.set(ruleSets);
        }


        try (FileAnalysisListener listener = ruleContext.startFileAnalysis(dataSource)) {
            final RuleContext ruleCtx = RuleContext.create(listener);

            LanguageVersion langVersion = configuration.getLanguageVersionOfFile(file.getPath());

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Processing " + file);
            }

            // Coarse check to see if any RuleSet applies to file, will need to do a finer RuleSet specific check later
            if (ruleSets.applies(file)) {
                if (configuration.getAnalysisCache().isUpToDate(file)) {
                    reportCachedRuleViolations(ruleCtx, file);
                } else {
                    try {
                        processSource(ruleCtx, langVersion, ruleSets);
                    } catch (Exception e) {
                        configuration.getAnalysisCache().analysisFailed(file);
                        ruleCtx.reportError(new Report.ProcessingError(e, file.getPath()));
                        LOG.log(Level.FINE, "Exception while processing file: " + file, e);
                    }
                }
            }
        } catch (Exception e) {
            throw new FileAnalysisException("Exception while closing listener for file " + file, e);
        }

        TimeTracker.finishThread();
    }

    public void processSource(RuleContext ruleCtx, LanguageVersion languageVersion, RuleSets ruleSets) throws IOException, FileAnalysisException {
        String fullSource;
        try (InputStream stream = dataSource.getInputStream()) {
            fullSource = IOUtils.toString(stream, configuration.getSourceEncoding());
        }
        String filename = dataSource.getNiceFileName(false, null);

        try {
            ruleSets.start(ruleCtx);
            processSource(fullSource, ruleSets, ruleCtx, languageVersion, filename);
        } finally {
            ruleSets.end(ruleCtx);
        }

    }


    private void reportCachedRuleViolations(final RuleContext ctx, File file) {
        for (final RuleViolation rv : configuration.getAnalysisCache().getCachedViolations(file)) {
            ctx.addViolationNoSuppress(rv);
        }
    }

    private RootNode parse(Parser parser, ParserTask task) throws IOException {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.PARSER)) {
            return parser.parse(task);
        }
    }


    private void processSource(String sourceCode,
                               RuleSets ruleSets,
                               RuleContext ctx,
                               LanguageVersion languageVersion,
                               String filename) throws FileAnalysisException, IOException {

        ParserTask task = new ParserTask(
            languageVersion,
            filename,
            sourceCode,
            SemanticErrorReporter.noop(),
            configuration.getSuppressMarker()
        );

        Parser parser = languageVersion.getLanguageVersionHandler().getParser();

        RootNode rootNode = parse(parser, task);

        dependencyHelper.runLanguageSpecificStages(ruleSets, languageVersion, rootNode);

        ruleSets.apply(Collections.singletonList(rootNode), ctx);
    }

}
