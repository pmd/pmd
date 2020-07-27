/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
public abstract class AbstractPMDProcessor {

    protected final PMDConfiguration configuration;

    AbstractPMDProcessor(PMDConfiguration configuration) {
        this.configuration = configuration;
    }

    protected void renderReports(final List<Renderer> renderers, final Report report) {

        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.REPORTING)) {
            for (Renderer r : renderers) {
                r.renderFileReport(report);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @SuppressWarnings("PMD.CloseResource")
    public void processFiles(RuleSets rulesets, List<DataSource> files, RuleContext ctx, List<Renderer> renderers) {
        // the data sources must only be closed after the threads are finished
        // this is done manually without a try-with-resources
        try {
            for (final DataSource dataSource : files) {
                runAnalysis(new PmdRunnable(dataSource, renderers, ctx, rulesets, configuration));
            }

            // render base report first - general errors
            renderReports(renderers, ctx.getReport());

            // then add analysis results per file
            collectReports(renderers);
        } finally {
            // in case we analyzed files within Zip Files/Jars, we need to close them after
            // the analysis is finished
            for (DataSource dataSource : files) {
                IOUtils.closeQuietly(dataSource);
            }
        }
    }

    protected abstract void runAnalysis(PmdRunnable runnable);

    protected abstract void collectReports(List<Renderer> renderers);

    /**
     * Returns a new file processor. The strategy used for threading is
     * determined by {@link PMDConfiguration#getThreads()}.
     */
    public static AbstractPMDProcessor newFileProcessor(final PMDConfiguration configuration) {
        return configuration.getThreads() > 0 ? new MultiThreadProcessor(configuration)
                                              : new MonoThreadProcessor(configuration);
    }
}
