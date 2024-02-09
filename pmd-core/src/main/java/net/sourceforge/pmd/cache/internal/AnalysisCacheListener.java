/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import java.io.IOException;
import java.util.Collection;

import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.internal.RuleSets;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;

/**
 * Adapter to wrap {@link AnalysisCache} behaviour in a {@link GlobalAnalysisListener}.
 */
public class AnalysisCacheListener implements GlobalAnalysisListener {

    private final AnalysisCache cache;

    public AnalysisCacheListener(AnalysisCache cache, RuleSets ruleSets, ClassLoader classLoader,
                                 Collection<? extends TextFile> textFiles) {
        this.cache = cache;
        cache.checkValidity(ruleSets, classLoader, textFiles);
    }

    @Override
    public FileAnalysisListener startFileAnalysis(TextFile file) {
        // AnalysisCache instances are handled specially in PmdRunnable
        return FileAnalysisListener.noop();
    }

    @Override
    public void close() throws IOException {
        cache.persist();
    }

}
