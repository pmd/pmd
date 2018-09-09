/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.security;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public abstract class SecurityRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/security.xml";

    @Override
    public void setUp() {
        addRule(RULESET, getClass().getSimpleName().replaceFirst("Test$", ""));
    }
}
