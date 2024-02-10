/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// Copy from PMD 7 with minimized functionality.
// Only the methods called by maven-pmd-plugin are kept,
// but they do nothing. That means, that maven-pmd-plugin can't report deprecated
// rules properly anymore.
// The ruleset for actual PMD analysis is loaded by PMD itself later on, and not
// through this class.

package net.sourceforge.pmd;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.rule.RuleSet;

public final class RuleSetLoader {
    public RuleSetLoader warnDeprecated(boolean warn) {
        return this;
    }

    public List<RuleSet> loadFromResources(Collection<String> paths) {
        return Collections.emptyList();
    }

    public static RuleSetLoader fromPmdConfig(PMDConfiguration configuration) {
        return new RuleSetLoader();
    }
}
