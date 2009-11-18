/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.lang.xsl.rule.xpath;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class XPathRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "xsl-xpath";

    @Before
    public void setUp() {
        addRule(RULESET, "UseConcatOnce");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(XPathRulesTest.class);
    }
}
