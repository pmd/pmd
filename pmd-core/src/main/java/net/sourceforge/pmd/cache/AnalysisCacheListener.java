/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import java.io.IOException;

import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * Adapter to wrap {@link AnalysisCache} behaviour in a {@link GlobalAnalysisListener}.
 */
@Deprecated
@InternalApi
public class AnalysisCacheListener implements GlobalAnalysisListener {

    private final AnalysisCache cache;

    public AnalysisCacheListener(AnalysisCache cache, RuleSets ruleSets, ClassLoader classLoader) {
        this.cache = cache;
        cache.checkValidity(ruleSets, classLoader);
    }


    @Override
    public FileAnalysisListener startFileAnalysis(DataSource file) {
        return cache.startFileAnalysis(file);
    }

    @Override
    public void close() throws IOException {
        cache.persist();
    }

}
