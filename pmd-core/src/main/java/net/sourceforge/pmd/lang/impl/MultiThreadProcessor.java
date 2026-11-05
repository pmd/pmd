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

import org.slf4j.event.Level;

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
        int threadCount = task.getThreadCount();
        if (threadCount == 1) {
            task.getMessageReporter().log(Level.DEBUG, "Using 1 thread for analysis");
        } else {
            task.getMessageReporter().log(Level.DEBUG, "Using {0} threads for analysis", threadCount);
        }
        executor = Executors.newFixedThreadPool(threadCount, new PmdThreadFactory());
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
        Error firstError = null;
        try {
            try {
                for (Future<?> task : futureList) {
                    task.get();
                }
            } catch (ExecutionException e) {
                task.getMessageReporter().error("Unknown error occurred while executing a PmdRunnable: {0}",
                        e.getCause().toString(), e.getCause());
                if (e.getCause() instanceof Error) {
                    firstError = (Error) e.getCause();
                    // Fail fast: don't start the files that are still queued.
                    // But the files that are already running must finish before
                    // the error is rethrown, otherwise their reports arrive after
                    // the listeners have been closed (see #6869).
                    executor.shutdownNow();
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

        if (firstError != null) {
            addOtherErrorsAsSuppressed(firstError);
            throw firstError;
        }
    }

    /**
     * Other files may have failed with an error as well, while they were still
     * running when the first error was found. Only the futures that are done are
     * checked: the futures of the files that were never started never complete.
     */
    private void addOtherErrorsAsSuppressed(Error firstError) {
        for (Future<?> task : futureList) {
            if (!task.isDone() || task.isCancelled()) {
                continue;
            }
            try {
                task.get();
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                // Throwable doesn't override equals, so this compares identity
                if (cause instanceof Error && !cause.equals(firstError)) {
                    firstError.addSuppressed(cause);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
