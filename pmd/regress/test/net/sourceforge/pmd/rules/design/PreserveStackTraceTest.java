/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class PreserveStackTraceTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("design", "PreserveStackTrace");
    }
    
    public void test() throws Throwable {
        rule.setMessage("{0}");
        runTests(rule);
    }
}
