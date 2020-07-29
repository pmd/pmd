/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.sourceforge.pmd.PMDConfiguration;


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
    protected void runAnalysis(PmdRunnable runnable) {
        executor.submit(runnable);
    }

    @Override
    public void close() {
        super.close();
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
