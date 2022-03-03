/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import java.io.IOException;

import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * An analysis cache for incremental analysis.
 * Simultaneously manages the old version of the cache,
 * and the new, most up-to-date violation cache.
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

    @Override
    public void onConfigError(ConfigurationError error) {
        GlobalAnalysisListener.super.onConfigError(error);
    }
}
