/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import static java.util.Collections.emptyMap;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.List;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageProcessor.AnalysisTask;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * This is internal API!
 *
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 */
@InternalApi
public abstract class AbstractPMDProcessor implements AutoCloseable {

    protected final AnalysisTask task;
    protected final LanguageProcessor processor;

    AbstractPMDProcessor(AnalysisTask task, LanguageProcessor processor) {
        this.task = task;
        this.processor = processor;
    }

    /**
     * Analyse all files. Each text file is closed.
     */
    public abstract void processFiles();

    /**
     * Joins tasks and await completion of the analysis. After this, all
     * {@link TextFile}s must have been closed.
     */
    @Override
    public abstract void close();

    /**
     * Returns a new file processor. The strategy used for threading is
     * determined by {@link AnalysisTask#getThreads()}.
     */
    public static AbstractPMDProcessor newFileProcessor(AnalysisTask analysisTask,
                                                        LanguageProcessor processor) {
        return analysisTask.getThreadCount() > 1
               ? new MultiThreadProcessor(analysisTask, processor)
               : new MonoThreadProcessor(analysisTask, processor);
    }

    /**
     * This is provided as convenience for tests. The listener is not closed.
     * It executes the rulesets on this thread, without copying the rulesets.
     */
    @InternalApi
    public static void runSingleFile(List<RuleSet> ruleSets,
                                     TextFile file,
                                     GlobalAnalysisListener listener,
                                     PMDConfiguration configuration) throws Exception {
        RuleSets rsets = new RuleSets(ruleSets);
        MessageReporter reporter = configuration.getReporter();


        LanguageRegistry singletonReg= new LanguageRegistry(setOf(file.getLanguageVersion().getLanguage()));
        try (LanguageProcessorRegistry registry =
                 LanguageProcessorRegistry.create(singletonReg,
                                                  emptyMap(),
                                                  reporter)) {

            LanguageProcessor lprocessor = registry.getProcessor(file.getLanguageVersion().getLanguage());
            lprocessor.launchAnalysis(new AnalysisTask(
                rsets,
                listOf(file),
                listener,
                1,
                configuration.getAnalysisCache(),
                reporter
            )).close();

        }
    }
}
