/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnnecessaryTemporariesTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("basic", "UnnecessaryConversionTemporary");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "all glommed together", 6, rule),
        });
    }

    private static final String TEST1 =
            " public class Foo {" + PMD.EOL +
            "     void method (int x) {" + PMD.EOL +
            "        new Integer(x).toString(); " + PMD.EOL +
            "        new Long(x).toString(); " + PMD.EOL +
            "        new Float(x).toString(); " + PMD.EOL +
            "        new Byte((byte)x).toString(); " + PMD.EOL +
            "        new Double(x).toString(); " + PMD.EOL +
            "        new Short((short)x).toString(); " + PMD.EOL +
            "     }" + PMD.EOL +
            " }";
}
