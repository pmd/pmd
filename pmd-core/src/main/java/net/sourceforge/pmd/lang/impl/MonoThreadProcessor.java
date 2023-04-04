/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.lang.LanguageProcessor.AnalysisTask;
import net.sourceforge.pmd.lang.document.TextFile;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 */
final class MonoThreadProcessor extends AbstractPMDProcessor {

    MonoThreadProcessor(AnalysisTask task) {
        super(task);
    }

    @Override
    @SuppressWarnings("PMD.CloseResource") // closed by the PMDRunnable
    public void processFiles() {
        for (TextFile file : task.getFiles()) {
            new MonothreadRunnable(file, task).run();
        }
    }

    @Override
    public void close() {
        // nothing to do
    }

    static final class MonothreadRunnable extends PmdRunnable {

        private final RuleSets ruleSets;

        MonothreadRunnable(TextFile textFile, AnalysisTask task) {
            super(textFile, task);
            this.ruleSets = task.getRulesets();
        }


        @Override
        protected RuleSets getRulesets() {
            return ruleSets;
        }
    }
}
