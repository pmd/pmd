/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.typeresolution;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CloneMethodMustImplementCloneableTest extends SimpleAggregatorTst {
    private static final String RULESET = "java-typeresolution";

    @Override
    public void setUp() {
        addRule(RULESET, "CloneMethodMustImplementCloneable");
    }
}
