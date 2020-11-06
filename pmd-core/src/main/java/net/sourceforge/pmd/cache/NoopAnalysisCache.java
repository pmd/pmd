/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import java.io.File;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.stat.Metric;

/**
 * A NOOP analysis cache. Easier / safer than null-checking.
 *
 * @deprecated This is internal API, will be hidden with 7.0.0
 */
@Deprecated
@InternalApi
public class NoopAnalysisCache implements AnalysisCache {

    @Override
    public void ruleViolationAdded(final RuleViolation ruleViolation) {
        // noop
    }

    @Override
    public void metricAdded(final Metric metric) {
        // noop
    }

    @Override
    public void persist() {
        // noop
    }

    @Override
    public boolean isUpToDate(final File sourceFile) {
        return false;
    }

    @Override
    public void analysisFailed(final File sourceFile) {
        // noop
    }

    @Override
    public void checkValidity(final RuleSets ruleSets, final ClassLoader classLoader) {
        // noop
    }

    @Override
    public List<RuleViolation> getCachedViolations(File sourceFile) {
        return Collections.emptyList();
    }
}
