/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.design;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class PreserveStackTraceTest extends SimpleAggregatorTst {

    private Rule rule;

    @Before
    public void setUp() {
        rule = findRule("design", "PreserveStackTrace");
    }
    
    @Test
    public void test() throws Throwable {
        runTests(rule);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(PreserveStackTraceTest.class);
    }
}
