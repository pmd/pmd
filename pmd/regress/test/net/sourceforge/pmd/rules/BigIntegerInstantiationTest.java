/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class BigIntegerInstantiationTest extends SimpleAggregatorTst {
    private Rule rule14;
    private Rule rule15;

    public void setUp() {
        rule14 = findRule("basic", "BigIntegerInstantiation_1.4");
        rule15 = findRule("basic", "BigIntegerInstantiation_1.5");
    }

    public void testAll() {
        runTests(rule14);
        runTests(rule15);
    }        
}
