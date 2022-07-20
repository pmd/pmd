/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.lang.LanguageProcessor.AnalysisTask;
import net.sourceforge.pmd.lang.document.TextFile;


/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 */
final class MultiThreadProcessor extends AbstractPMDProcessor {

    private final ExecutorService executor;

    MultiThreadProcessor(final AnalysisTask task) {
        super(task);

        executor = Executors.newFixedThreadPool(task.getThreadCount(), new PmdThreadFactory());
    }

    @Override
    @SuppressWarnings("PMD.CloseResource") // closed by the PMDRunnable
    public void processFiles() {
        // The thread-local is not static, but analysis-global
        // This means we don't have to reset it manually, every analysis is isolated.
        // The initial value makes a copy of the rulesets
        final ThreadLocal<RuleSets> ruleSetCopy = ThreadLocal.withInitial(() -> new RuleSets(task.getRulesets()));

        for (final TextFile textFile : task.getFiles()) {
            executor.submit(new PmdRunnable(textFile, task) {
                @Override
                protected RuleSets getRulesets() {
                    return ruleSetCopy.get();
                }
            });
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
