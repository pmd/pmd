/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xsl.rule.xpath;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class XPathRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "xsl-xpath";

    @Override
    public void setUp() {
        addRule(RULESET, "UseConcatOnce");
        addRule(RULESET, "AvoidAxisNavigation");
    }
}
