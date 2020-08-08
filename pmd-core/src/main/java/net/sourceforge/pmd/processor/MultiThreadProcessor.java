/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.datasource.DataSource;


/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 */
final class MultiThreadProcessor extends AbstractPMDProcessor {
    private final ExecutorService executor;

    MultiThreadProcessor(final PMDConfiguration configuration) {
        super(configuration);

        executor = Executors.newFixedThreadPool(configuration.getThreads(), new PmdThreadFactory());
    }

    @Override
    public void processFiles(RuleSets rulesets, List<DataSource> files, GlobalAnalysisListener listener) {
        // The thread-local is not static, but analysis-global
        // This means we don't have to reset it manually, every analysis is isolated.
        // The initial value makes a copy of the rulesets
        final ThreadLocal<RuleSets> ruleSetCopy = ThreadLocal.withInitial(() -> new RuleSets(rulesets));

        for (final DataSource dataSource : files) {
            executor.submit(new PmdRunnable(dataSource, listener, ruleSetCopy, configuration));
        }
    }

    @Override
    public void close() {
        try {
            executor.shutdown();
            while (!executor.awaitTermination(10, TimeUnit.HOURS)) {
                // still waiting
                Thread.yield();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdownNow();
        }
    }
}
