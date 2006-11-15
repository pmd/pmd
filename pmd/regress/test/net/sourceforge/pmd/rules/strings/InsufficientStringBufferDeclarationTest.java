/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class InsufficientStringBufferDeclarationTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("strings", "InsufficientStringBufferDeclaration");
    }

    public void testAll() {
        runTests(rule);
    }
}