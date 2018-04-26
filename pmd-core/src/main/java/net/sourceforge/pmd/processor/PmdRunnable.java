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

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.datasource.DataSource;

public class PmdRunnable implements Callable<Report> {

    private static final Logger LOG = Logger.getLogger(PmdRunnable.class.getName());

    private final DataSource dataSource;
    private final String fileName;
    private final List<Renderer> renderers;
    private final RuleContext ruleContext;
    private final RuleSets ruleSets;
    private final SourceCodeProcessor sourceCodeProcessor;

    public PmdRunnable(DataSource dataSource, String fileName, List<Renderer> renderers,
            RuleContext ruleContext, RuleSets ruleSets, SourceCodeProcessor sourceCodeProcessor) {
        this.ruleSets = ruleSets;
        this.dataSource = dataSource;
        this.fileName = fileName;
        this.renderers = renderers;
        this.ruleContext = ruleContext;
        this.sourceCodeProcessor = sourceCodeProcessor;
    }

    @Deprecated
    public static void reset() {
        /*
        This method is useless for the next major release, as the thread context is not saved in a thread  local anymore
         */
    }

    private void addError(Report report, Exception e, String errorMessage) {
        // unexpected exception: log and stop executor service
        LOG.log(Level.FINE, errorMessage, e);
        report.addError(new Report.ProcessingError(e, fileName));
    }

    @Override
    public Report call() {
        final RuleSets ruleSetsCopy = new RuleSets(ruleSets);
        final RuleContext ruleContextCopy = new RuleContext(ruleContext);

        Report report = Report.createReport(ruleContextCopy, fileName);

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Processing " + ruleContextCopy.getSourceCodeFilename());
        }
        for (Renderer r : renderers) {
            r.startFileAnalysis(dataSource);
        }

        try (InputStream stream = new BufferedInputStream(dataSource.getInputStream())) {
            ruleContextCopy.setLanguageVersion(null);
            sourceCodeProcessor.processSourceCode(stream, ruleSetsCopy, ruleContextCopy);
        } catch (PMDException pmde) {
            addError(report, pmde, "Error while processing file: " + fileName);
        } catch (IOException ioe) {
            addError(report, ioe, "IOException during processing of " + fileName);
        } catch (RuntimeException re) {
            addError(report, re, "RuntimeException during processing of " + fileName);
        }

        return report;
    }
}
