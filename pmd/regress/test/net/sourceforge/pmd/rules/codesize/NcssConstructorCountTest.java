/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.codesize;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class NcssConstructorCountTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() {
        rule = findRule("codesize", "NcssConstructorCount");
    }

    public void testAll() {
        rule.addProperty("minimum", "13");
        runTests(rule);
    }
}

