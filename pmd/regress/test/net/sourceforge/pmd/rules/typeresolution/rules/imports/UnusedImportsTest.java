/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.typeresolution.rules.imports;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class UnusedImportsTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("typeresolution", "UnusedImports");
        rule.setMessage("{0}");
        rule.setUsesTypeResolution();
    }

    public void testAll() {
        runTests(rule);
    }
}
