package net.sourceforge.pmd.cache;

import java.io.File;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.stat.Metric;

/**
 * A NOOP analysis cache. Easier / safer than null-checking. 
 */
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
}
