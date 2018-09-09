/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xsl.rule.codestyle;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public abstract class CodeStyleRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/xsl/codestyle.xml";

    @Override
    public void setUp() {
        addRule(RULESET, getClass().getSimpleName().replaceFirst("Test$", ""));
    }
}
