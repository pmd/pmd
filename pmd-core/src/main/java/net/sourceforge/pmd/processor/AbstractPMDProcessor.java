/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageProcessor.AnalysisTask;
import net.sourceforge.pmd.lang.document.TextFile;

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

}
