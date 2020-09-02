/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.List;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.document.TextFile;

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
    public void processFiles(RuleSets rulesets, List<TextFile> files, GlobalAnalysisListener listener) {
        for (TextFile file : files) {
            new PmdRunnable(file, listener, configuration) {
                @Override
                protected RuleSets getRulesets() {
                    return rulesets;
                }
            }.run();
        }
    }

    @Override
    public void close() {
        // nothing to do
    }
}
