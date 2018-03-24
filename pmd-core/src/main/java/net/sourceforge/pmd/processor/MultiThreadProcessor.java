/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.renderers.Renderer;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
public class MultiThreadProcessor extends AbstractPMDProcessor {

    private ExecutorService executor;
    private CompletionService<Report> completionService;
    private List<Future<Report>> tasks = new ArrayList<>();

    public MultiThreadProcessor(final PMDConfiguration configuration) {
        super(configuration);

        executor = Executors.newFixedThreadPool(configuration.getThreads(), new PmdThreadFactory());
        completionService = new ExecutorCompletionService<>(executor);
    }

    @Override
    protected void runAnalysis(PmdRunnable runnable) {
        // multi-threaded execution, dispatch analysis to worker threads
        tasks.add(completionService.submit(runnable));
    }

    @Override
    protected void collectReports(List<Renderer> renderers) {
        // Collect result analysis, waiting for termination if needed
        try {
            for (int i = 0; i < tasks.size(); i++) {
                final Report report = completionService.take().get();
                super.renderReports(renderers, report);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException ee) {
            Throwable t = ee.getCause();
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
