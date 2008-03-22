/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class UselessAssignmentRuleTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        // addRule("scratchpad", "UselessAssignment");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(
                UselessAssignmentRuleTest.class);
    }
}
