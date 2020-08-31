/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import net.sourceforge.pmd.RuleViolation;

final class NoopFileListener implements FileAnalysisListener {

    static final NoopFileListener INSTANCE = new NoopFileListener();

    private NoopFileListener() {
        // singleton
    }

    @Override
    public void onRuleViolation(RuleViolation violation) {
        // do nothing
    }

    @Override
    public String toString() {
        return "Noop";
    }
}
