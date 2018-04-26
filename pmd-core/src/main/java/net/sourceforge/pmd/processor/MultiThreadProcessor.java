/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.renderers.Renderer;


/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 */
public class MultiThreadProcessor extends AbstractPMDProcessor {
    private final ExecutorService executor;
    private final CompletionService<Report> completionService;

    private long submittedTasks = 0L;

    public MultiThreadProcessor(final PMDConfiguration configuration) {
        super(configuration);

        executor = Executors.newFixedThreadPool(configuration.getThreads(), new PmdThreadFactory());
        completionService = new ExecutorCompletionService<>(executor);
    }

    @Override
    protected void runAnalysis(PmdRunnable runnable) {
        completionService.submit(runnable);
        submittedTasks++;
    }

    @Override
    protected void collectReports(List<Renderer> renderers) {
        try {
            for (int i = 0; i < submittedTasks; i++) {
                final Report report = completionService.take().get();
                super.renderReports(renderers, report);
            }
        } catch (final InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (final ExecutionException ee) {
            final Throwable t = ee.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else {
                throw new IllegalStateException("PmdRunnable exception", t);
            }
        } finally {
            executor.shutdownNow();
        }
    }
}
