/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.document.io.TextFile;

/**
 * This is internal API!
 *
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 */
public abstract class AbstractPMDProcessor implements AutoCloseable {

    protected final PMDConfiguration configuration;

    AbstractPMDProcessor(PMDConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Analyse all files. Each text file is closed.
     */
    public abstract void processFiles(RuleSets rulesets, List<TextFile> files, GlobalAnalysisListener listener);

    /**
     * Joins tasks and await completion of the analysis.
     */
    @Override
    public abstract void close();

    /**
     * Returns a new file processor. The strategy used for threading is
     * determined by {@link PMDConfiguration#getThreads()}.
     */
    public static AbstractPMDProcessor newFileProcessor(final PMDConfiguration configuration) {
        return configuration.getThreads() > 1 ? new MultiThreadProcessor(configuration)
                                              : new MonoThreadProcessor(configuration);
    }

    /**
     * This is provided as convenience for tests. The listener is not closed.
     * It executes the rulesets on this thread, without copying the rulesets.
     */
    @InternalApi
    public static void runSingleFile(List<RuleSet> ruleSets, TextFile file, GlobalAnalysisListener listener, PMDConfiguration configuration) {
        RuleSets rsets = new RuleSets(ruleSets);
        new MonoThreadProcessor(configuration).processFiles(rsets, listOf(file), listener);
    }
}
