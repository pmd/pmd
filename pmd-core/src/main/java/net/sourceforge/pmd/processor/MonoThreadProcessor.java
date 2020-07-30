/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.List;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
final class MonoThreadProcessor extends AbstractPMDProcessor {

    MonoThreadProcessor(PMDConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void processFiles(RuleSets rulesets, List<DataSource> files, GlobalAnalysisListener listener) {
        // populating the initial value avoids copying the ruleset
        ThreadLocal<RuleSets> tlocal = ThreadLocal.withInitial(() -> rulesets);
        for (DataSource file : files) {
            new PmdRunnable(file, listener, tlocal, configuration).run();
        }
    }

    public void close() {
        // nothing to do
    }
}
