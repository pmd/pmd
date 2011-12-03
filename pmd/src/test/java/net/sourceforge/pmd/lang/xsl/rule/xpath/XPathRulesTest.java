/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xsl.rule.xpath;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class XPathRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "xsl-xpath";

    @Before
    public void setUp() {
        addRule(RULESET, "UseConcatOnce");
	addRule(RULESET, "AvoidAxisNavigation");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(XPathRulesTest.class);
    }
}
