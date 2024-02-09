/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.sourceforge.pmd.lang.LanguageProcessor.AnalysisTask;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.internal.RuleSets;
import net.sourceforge.pmd.util.log.PmdReporter;


/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 */
final class MultiThreadProcessor extends AbstractPMDProcessor {
    private final ExecutorService executor;

    private final List<Future<?>> futureList;

    MultiThreadProcessor(final AnalysisTask task) {
        super(task);

        executor = Executors.newFixedThreadPool(task.getThreadCount(), new PmdThreadFactory());
        futureList = new LinkedList<>();
    }

    @Override
    @SuppressWarnings("PMD.CloseResource") // closed by the PMDRunnable
    public void processFiles() {
        // The thread-local is not static, but analysis-global
        // This means we don't have to reset it manually, every analysis is isolated.
        // The initial value makes a copy of the rulesets
        final ThreadLocal<RuleSets> ruleSetCopy = ThreadLocal.withInitial(() -> {
            RuleSets copy = new RuleSets(task.getRulesets());
            // use a noop reporter because the copy should only contain rules that
            // initialized properly
            copy.initializeRules(task.getLpRegistry(), PmdReporter.quiet());
            return copy;
        });

        for (final TextFile textFile : task.getFiles()) {
            futureList.add(executor.submit(new PmdRunnable(textFile, task) {
                @Override
                protected RuleSets getRulesets() {
                    return ruleSetCopy.get();
                }
            }));
        }
    }

    @Override
    public void close() {
        try {
            try {
                for (Future<?> task : futureList) {
                    task.get();
                }
            } catch (ExecutionException e) {
                task.getMessageReporter().error("Unknown error occurred while executing a PmdRunnable: {0}",
                        e.getCause().toString(), e.getCause());
                if (e.getCause() instanceof Error) {
                    throw (Error) e.getCause();
                }
            }

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
