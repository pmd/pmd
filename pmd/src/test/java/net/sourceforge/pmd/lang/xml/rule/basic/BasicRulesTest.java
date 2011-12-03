/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xml.rule.basic;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class BasicRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "xml-basic";

    @Before
    public void setUp() {
        addRule(RULESET, "MistypedCDATASection");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(BasicRulesTest.class);
    }
}
