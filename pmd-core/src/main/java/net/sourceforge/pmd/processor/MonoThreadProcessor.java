/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.renderers.Renderer;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
public final class MonoThreadProcessor extends AbstractPMDProcessor {

    private final List<Report> reports = new ArrayList<>();

    public MonoThreadProcessor(PMDConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected void runAnalysis(PmdRunnable runnable) {
        // single thread execution, run analysis on same thread
        reports.add(runnable.call());
    }

    @Override
    protected void collectReports(List<Renderer> renderers) {
        for (Report r : reports) {
            super.renderReports(renderers, r);
        }

        // Since this thread may run PMD again, clean up the runnable
        PmdRunnable.reset();
    }
}
