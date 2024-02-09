/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.cache.internal.AnalysisCache;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.internal.RuleSets;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.log.PmdReporter;

/**
 * Internal API.
 *
 * <p>Acts as a bridge between outer parts of PMD and the restricted access
 * internal API of this package.
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 * Use this only at your own risk.
 *
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {
    private InternalApiBridge() {}

    public static LanguageProcessor.AnalysisTask createAnalysisTask(RuleSets rulesets,
                                                                    List<TextFile> files,
                                                                    GlobalAnalysisListener listener,
                                                                    int threadCount,
                                                                    AnalysisCache analysisCache,
                                                                    PmdReporter messageReporter,
                                                                    LanguageProcessorRegistry lpRegistry) {
        return new LanguageProcessor.AnalysisTask(rulesets, files, listener, threadCount, analysisCache, messageReporter, lpRegistry);
    }

    public static LanguageProcessor.AnalysisTask taskWithFiles(LanguageProcessor.AnalysisTask originalTask, List<TextFile> newFiles) {
        return originalTask.withFiles(newFiles);
    }
}
