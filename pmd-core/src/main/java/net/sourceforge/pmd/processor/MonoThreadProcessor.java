/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import net.sourceforge.pmd.PMDConfiguration;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
final class MonoThreadProcessor extends AbstractPMDProcessor {

    MonoThreadProcessor(PMDConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected void runAnalysis(PmdRunnable runnable) {
        // single thread execution, run analysis on same thread
        runnable.run();
    }

}
