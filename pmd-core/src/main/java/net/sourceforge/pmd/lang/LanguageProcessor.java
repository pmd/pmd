/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.List;

import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.cache.AnalysisCache;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * Stateful object managing the analysis.
 *
 * @author Clément Fournier
 */
public interface LanguageProcessor extends AutoCloseable {

    LanguageVersionHandler services();

    AutoCloseable launchAnalysis(
        AnalysisTask analysisTask
    );

    Language getLanguage();

    class AnalysisTask {

        private final RuleSets rulesets;
        private final List<TextFile> files;
        private final GlobalAnalysisListener listener;
        private int threadCount;
        private final AnalysisCache analysisCache;
        private final MessageReporter messageReporter;


        public AnalysisTask(RuleSets rulesets,
                            List<TextFile> files,
                            GlobalAnalysisListener listener,
                            int threadCount,
                            AnalysisCache analysisCache,
                            MessageReporter messageReporter) {
            this.rulesets = rulesets;
            this.files = files;
            this.listener = listener;
            this.threadCount = threadCount;
            this.analysisCache = analysisCache;
            this.messageReporter = messageReporter;
        }

        public RuleSets getRulesets() {
            return rulesets;
        }

        public List<TextFile> getFiles() {
            return files;
        }

        public GlobalAnalysisListener getListener() {
            return listener;
        }

        public int getThreadCount() {
            return threadCount;
        }

        public AnalysisCache getAnalysisCache() {
            return analysisCache;
        }

        public MessageReporter getMessageReporter() {
            return messageReporter;
        }
    }


}
