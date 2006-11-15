/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.imports;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class UnusedImportsRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("imports", "UnusedImports");
    }

    public void testAll() {
        runTests(rule);
    }
}
