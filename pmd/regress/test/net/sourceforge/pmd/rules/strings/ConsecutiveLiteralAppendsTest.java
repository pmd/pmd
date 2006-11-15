/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class ConsecutiveLiteralAppendsTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() {
        rule = findRule("strings", "ConsecutiveLiteralAppends");
    }

    public void testAll() {
        runTests(rule);
    }
}