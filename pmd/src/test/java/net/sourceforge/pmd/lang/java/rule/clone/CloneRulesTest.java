/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.clone;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CloneRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-clone";

    @Override
    public void setUp() {
        addRule(RULESET, "CloneMethodMustImplementCloneable");
        addRule(RULESET, "CloneThrowsCloneNotSupportedException");
        addRule(RULESET, "ProperCloneImplementation");
    }
}
