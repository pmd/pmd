/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.basic;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the basic ruleset
 */
public class BasicRulesTest extends SimpleAggregatorTst {

	private static final String RULESET = "apex-basic";

	@Override
	public void setUp() {
		addRule(RULESET, "AvoidSoqlInLoops");
	}
}
