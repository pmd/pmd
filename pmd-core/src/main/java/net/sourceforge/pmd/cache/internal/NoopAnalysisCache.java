/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.internal.RuleSets;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.RuleViolation;

/**
 * A NOOP analysis cache. Easier / safer than null-checking.
 */
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
