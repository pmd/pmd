/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.datasource.DataSource;

public class PmdRunnable implements Callable<Report> {

    private static final Logger LOG = Logger.getLogger(PmdRunnable.class.getName());

    private static final ThreadLocal<ThreadContext> LOCAL_THREAD_CONTEXT = new ThreadLocal<>();

    private final PMDConfiguration configuration;
    private final DataSource dataSource;
    private final String fileName;
    private final List<Renderer> renderers;
    private final RuleContext ruleContext;
    private final RuleSetFactory ruleSetFactory;
    private final SourceCodeProcessor sourceCodeProcessor;

    public PmdRunnable(PMDConfiguration configuration, DataSource dataSource, String fileName,
            List<Renderer> renderers, RuleContext ruleContext, RuleSetFactory ruleSetFactory,
            SourceCodeProcessor sourceCodeProcessor) {
        this.configuration = configuration;
        this.dataSource = dataSource;
        this.fileName = fileName;
        this.renderers = renderers;
        this.ruleContext = ruleContext;
        this.ruleSetFactory = ruleSetFactory;
        this.sourceCodeProcessor = sourceCodeProcessor;
    }

    public static void reset() {
        LOCAL_THREAD_CONTEXT.remove();
    }

    private void addError(Report report, Exception e, String errorMessage) {
        // unexpected exception: log and stop executor service
        LOG.log(Level.FINE, errorMessage, e);
        report.addError(new Report.ProcessingError(e, fileName));
    }

    @Override
    public Report call() {
        ThreadContext tc = LOCAL_THREAD_CONTEXT.get();
        if (tc == null) {
            try {
                tc = new ThreadContext(ruleSetFactory.createRuleSets(configuration.getRuleSets()),
                        new RuleContext(ruleContext));
            } catch (RuleSetNotFoundException e) {
                throw new RuntimeException(e);
            }
            LOCAL_THREAD_CONTEXT.set(tc);
        }

        Report report = PMD.setupReport(tc.ruleSets, tc.ruleContext, fileName);

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Processing " + tc.ruleContext.getSourceCodeFilename());
        }
        for (Renderer r : renderers) {
            r.startFileAnalysis(dataSource);
        }

        try {
            InputStream stream = new BufferedInputStream(dataSource.getInputStream());
            tc.ruleContext.setLanguageVersion(null);
            sourceCodeProcessor.processSourceCode(stream, tc.ruleSets, tc.ruleContext);
        } catch (PMDException pmde) {
            addError(report, pmde, "Error while processing file: " + fileName);
        } catch (IOException ioe) {
            addError(report, ioe, "IOException during processing of " + fileName);
        } catch (RuntimeException re) {
            addError(report, re, "RuntimeException during processing of " + fileName);
        }

        return report;
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
