/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.reporting.FileAnalysisListener;

/**
 * A NOOP analysis cache. Easier / safer than null-checking.
 *
 * @deprecated This is internal API, will be hidden with 7.0.0
 */
@Deprecated
@InternalApi
public class NoopAnalysisCache implements AnalysisCache {

    @Override
    public void persist() {
        // noop
    }

    @Override
    public boolean isUpToDate(final TextDocument document) {
        return false;
    }

    @Override
    public void analysisFailed(final TextDocument sourceFile) {
        // noop
    }

    @Override
    public void checkValidity(RuleSets ruleSets, ClassLoader auxclassPathClassLoader, Collection<? extends TextFile> files) {
        // noop
    }

    @Override
    public List<RuleViolation> getCachedViolations(TextDocument sourceFile) {
        return Collections.emptyList();
    }

    @Override
    public FileAnalysisListener startFileAnalysis(TextDocument filename) {
        return FileAnalysisListener.noop();
    }

}
