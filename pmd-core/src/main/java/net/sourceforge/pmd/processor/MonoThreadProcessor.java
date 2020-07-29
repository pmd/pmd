/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
final class MonoThreadProcessor extends AbstractPMDProcessor {

    private final List<Report> reports = new ArrayList<>();

    public MonoThreadProcessor(PMDConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected void runAnalysis(PmdRunnable runnable) {
        // single thread execution, run analysis on same thread
        runnable.run();
    }

}
