/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.javabeans;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class JavabeansRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-javabeans";

    @Override
    public void setUp() {
        addRule(RULESET, "BeanMembersShouldSerialize");
        addRule(RULESET, "MissingSerialVersionUID");
    }
}
