/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.cache.AnalysisCache;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * Stateful object managing the analysis for a given language.
 *
 * @author Clément Fournier
 */
public interface LanguageProcessor extends AutoCloseable {

    /**
     * A collection of extension points implemented by the language.
     */
    @NonNull LanguageVersionHandler services();

    /**
     * Launch the analysis based on the given {@link AnalysisTask analysis task}.
     * The analysis only has to completion after the return value has been closed,
     * as this method may launch background threads to perform the analysis and
     * return without blocking. In that case the returned Closeable will join the
     * analysis threads when being closed.
     *
     * @param analysisTask Configuration of the analysis
     *
     * @return A closeable - the analysis is only ended when the close method returns.
     */
    @NonNull AutoCloseable launchAnalysis(
        @NonNull AnalysisTask analysisTask
    );

    /**
     * The language of this processor.
     */
    @NonNull Language getLanguage();

    /**
     * The language version that was configured when creating this processor.
     */
    @NonNull LanguageVersion getLanguageVersion();

    /**
     * Configuration of an analysis, as given to {@link #launchAnalysis(AnalysisTask)}.
     * This includes eg the set of files to process (which may be of various languages),
     * the cache manager, and the rulesets.
     */
    class AnalysisTask {

        private final RuleSets rulesets;
        private final List<TextFile> files;
        private final GlobalAnalysisListener listener;
        private final int threadCount;
        private final AnalysisCache analysisCache;
        private final MessageReporter messageReporter;
        private final LanguageProcessorRegistry lpRegistry;


        /**
         * Create a new task. This constructor is internal and will be
         * called by PMD.
         */
        @InternalApi
        public AnalysisTask(RuleSets rulesets,
                            List<TextFile> files,
                            GlobalAnalysisListener listener,
                            int threadCount,
                            AnalysisCache analysisCache,
                            MessageReporter messageReporter,
                            LanguageProcessorRegistry lpRegistry) {
            this.rulesets = rulesets;
            this.files = files;
            this.listener = listener;
            this.threadCount = threadCount;
            this.analysisCache = analysisCache;
            this.messageReporter = messageReporter;
            this.lpRegistry = lpRegistry;
        }

        public RuleSets getRulesets() {
            return rulesets;
        }

        public List<TextFile> getFiles() {
            return Collections.unmodifiableList(files);
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

        public LanguageProcessorRegistry getLpRegistry() {
            return lpRegistry;
        }

        /**
         * Produce a new analysis task with just different files.
         */
        public AnalysisTask withFiles(List<TextFile> newFiles) {
            return new AnalysisTask(
                rulesets,
                newFiles,
                listener,
                threadCount,
                analysisCache,
                messageReporter,
                lpRegistry
            );
        }
    }


}
