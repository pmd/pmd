/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.Collection;

import net.sourceforge.pmd.lang.rule.RuleSet;

public class RuleSets extends net.sourceforge.pmd.RuleSets {
    public RuleSets(net.sourceforge.pmd.RuleSets ruleSets) {
        super(ruleSets);
    }

    public RuleSets(net.sourceforge.pmd.lang.rule.internal.RuleSets ruleSets) {
        super(ruleSets);
    }

    public RuleSets(Collection<? extends RuleSet> ruleSets) {
        super(ruleSets);
    }

    public RuleSets(RuleSet ruleSet) {
        super(ruleSet);
    }
}
