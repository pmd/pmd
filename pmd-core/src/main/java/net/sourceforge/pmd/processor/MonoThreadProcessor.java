/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.List;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
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
    @SuppressWarnings("PMD.CloseResource") // closed by the PMDRunnable
    public void processFiles(RuleSets rulesets, List<DataSource> files, GlobalAnalysisListener listener) {
        for (DataSource file : files) {
            new MonothreadRunnable(rulesets, file, listener, configuration).run();
        }
    }

    @Override
    public void close() {
        // nothing to do
    }

    static final class MonothreadRunnable extends PmdRunnable {

        private final RuleSets ruleSets;

        MonothreadRunnable(RuleSets ruleSets, DataSource dataSource, GlobalAnalysisListener ruleContext, PMDConfiguration configuration) {
            super(dataSource, ruleContext, configuration);
            this.ruleSets = ruleSets;
        }

        @Override
        protected RuleSets getRulesets() {
            return ruleSets;
        }
    }
}
