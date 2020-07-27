/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
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
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.datasource.DataSource;

public class PmdRunnable implements Callable<Report> {

    private static final Logger LOG = Logger.getLogger(PmdRunnable.class.getName());

    private static final ThreadLocal<ThreadContext> LOCAL_THREAD_CONTEXT = new ThreadLocal<>();

    private final DataSource dataSource;
    private final File file;
    private final List<Renderer> renderers;
    private final RuleContext ruleContext;
    private final RuleSets ruleSets;
    private final PMDConfiguration configuration;

    private final RulesetStageDependencyHelper dependencyHelper;

    public PmdRunnable(DataSource dataSource,
                       List<Renderer> renderers,
                       RuleContext ruleContext,
                       List<RuleSet> ruleSets,
                       PMDConfiguration configuration) {
        this(dataSource, renderers, ruleContext, new RuleSets(ruleSets), configuration);
    }

    public PmdRunnable(DataSource dataSource,
                       List<Renderer> renderers,
                       RuleContext ruleContext,
                       RuleSets ruleSets,
                       PMDConfiguration configuration) {
        this.ruleSets = ruleSets;
        this.dataSource = dataSource;
        // this is the real, canonical and absolute filename (not shortened)
        String realFileName = dataSource.getNiceFileName(false, null);

        this.file = new File(realFileName);
        this.renderers = renderers;
        this.ruleContext = ruleContext;
        this.configuration = configuration;
        this.dependencyHelper = new RulesetStageDependencyHelper(configuration);
    }

    public static void reset() {
        LOCAL_THREAD_CONTEXT.remove();
    }

    @Override
    public Report call() {
        TimeTracker.initThread();

        ThreadContext tc = LOCAL_THREAD_CONTEXT.get();
        if (tc == null) {
            tc = new ThreadContext(new RuleSets(ruleSets), new RuleContext(ruleContext));
            LOCAL_THREAD_CONTEXT.set(tc);
        }
        RuleContext ruleCtx = tc.ruleContext;
        LanguageVersion langVersion =
            ruleContext.getLanguageVersion() != null ? ruleCtx.getLanguageVersion()
                                                     : configuration.getLanguageVersionOfFile(file.getPath());

        Report report = new Report();
        // overtake the listener
        report.addListeners(ruleCtx.getReport().getListeners());
        ruleCtx.setReport(report);
        ruleCtx.setSourceCodeFile(file);
        ruleCtx.setLanguageVersion(langVersion);

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Processing " + file);
        }

        for (Renderer r : renderers) {
            r.startFileAnalysis(dataSource);
        }


        // Coarse check to see if any RuleSet applies to file, will need to do a finer RuleSet specific check later
        if (ruleSets.applies(file)) {
            if (configuration.getAnalysisCache().isUpToDate(file)) {
                reportCachedRuleViolations(ruleCtx);
            } else {
                try {
                    processSource(ruleCtx, langVersion);
                } catch (Exception e) {
                    configuration.getAnalysisCache().analysisFailed(file);
                    report.addError(new Report.ProcessingError(e, file.getPath()));

                    if (ruleCtx.isIgnoreExceptions()) {
                        LOG.log(Level.FINE, "Exception while processing file: " + file, e);
                    } else {
                        if (e instanceof FileAnalysisException) {
                            throw (FileAnalysisException) e;
                        }
                        throw new FileAnalysisException(e);
                    }
                }
            }
        }

        TimeTracker.finishThread();

        return report;
    }

    public void processSource(RuleContext ruleCtx, LanguageVersion languageVersion) throws IOException, FileAnalysisException {
        String fullSource;
        try (InputStream stream = dataSource.getInputStream()) {
            fullSource = IOUtils.toString(stream, configuration.getSourceEncoding());
        }

        try {
            ruleSets.start(ruleCtx);
            processSource(fullSource, ruleSets, ruleCtx, languageVersion);
        } finally {
            ruleSets.end(ruleCtx);
        }

    }


    private void reportCachedRuleViolations(final RuleContext ctx) {
        for (final RuleViolation rv : configuration.getAnalysisCache().getCachedViolations(ctx.getSourceCodeFile())) {
            ctx.getReport().addRuleViolation(rv);
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
                               LanguageVersion languageVersion) throws FileAnalysisException, IOException {

        ParserTask task = new ParserTask(
            languageVersion,
            String.valueOf(ctx.getSourceCodeFile()),
            sourceCode,
            SemanticErrorReporter.noop(),
            configuration.getSuppressMarker()
        );

        Parser parser = languageVersion.getLanguageVersionHandler().getParser();

        RootNode rootNode = parse(parser, task);

        dependencyHelper.runLanguageSpecificStages(ruleSets, languageVersion, rootNode);

        ruleSets.apply(Collections.singletonList(rootNode), ctx);
    }

    private static class ThreadContext {

        /* default */ final RuleSets ruleSets;
        /* default */ final RuleContext ruleContext;

        ThreadContext(RuleSets ruleSets, RuleContext ruleContext) {
            this.ruleSets = ruleSets;
            this.ruleContext = ruleContext;
        }
    }
}
