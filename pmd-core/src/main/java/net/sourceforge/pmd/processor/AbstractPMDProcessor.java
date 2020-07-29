/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.List;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 */
public abstract class AbstractPMDProcessor implements AutoCloseable {

    protected final PMDConfiguration configuration;

    AbstractPMDProcessor(PMDConfiguration configuration) {
        this.configuration = configuration;
    }

    @SuppressWarnings("PMD.CloseResource")
    public void processFiles(RuleSets rulesets, List<DataSource> files, GlobalAnalysisListener ctx) {
        // the data sources must only be closed after the threads are finished
        // this is done manually without a try-with-resources
        try {
            for (final DataSource dataSource : files) {
                runAnalysis(new PmdRunnable(dataSource, ctx, rulesets, configuration));
            }
        } finally {
            // in case we analyzed files within Zip Files/Jars, we need to close them after
            // the analysis is finished
            for (DataSource dataSource : files) {
                IOUtils.closeQuietly(dataSource);
            }
        }
    }

    protected abstract void runAnalysis(PmdRunnable runnable);

    /**
     * Joins tasks and await completion of the analysis.
     */
    @Override
    public void close() {
        // to be overridden
    }

    /**
     * Returns a new file processor. The strategy used for threading is
     * determined by {@link PMDConfiguration#getThreads()}.
     */
    public static AbstractPMDProcessor newFileProcessor(final PMDConfiguration configuration) {
        return configuration.getThreads() > 0 ? new MultiThreadProcessor(configuration)
                                              : new MonoThreadProcessor(configuration);
    }
}
