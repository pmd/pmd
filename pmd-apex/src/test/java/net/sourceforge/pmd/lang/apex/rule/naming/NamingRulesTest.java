/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.naming;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class NamingRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "apex-naming";

    @Override
    public void setUp() {
//		  addRule(RULESET, "ClassNamingConventions");
//		  addRule(RULESET, "MethodNamingConventions");
		  addRule(RULESET, "VariableNamingConventions");
//		  addRule(RULESET, "MethodWithSameNameAsEnclosingClass");
    }
}
