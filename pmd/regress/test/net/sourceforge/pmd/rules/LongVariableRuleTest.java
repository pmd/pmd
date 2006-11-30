/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class LongVariableRuleTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() {
        rule = findRule("naming", "LongVariable");
    }

    public void testAll() {
        runTests(rule);
    }
}
