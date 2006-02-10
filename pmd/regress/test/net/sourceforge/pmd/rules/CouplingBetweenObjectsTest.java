/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class CouplingBetweenObjectsTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("coupling", "CouplingBetweenObjects");
        rule.addProperty("threshold", "2");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "lots of coupling", 1, rule),
            new TestDescriptor(TEST2, "no coupling", 0, rule),
            new TestDescriptor(TEST3, "skip interfaces", 0, rule),
        });
    }

    private static final String TEST1 =
            "import java.util.*;" + PMD.EOL +
            "public class Foo {" + PMD.EOL +
            " public List foo() {return null;}" + PMD.EOL +
            " public ArrayList foo() {return null;}" + PMD.EOL +
            " public Vector foo() {return null;}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public interface Foo {" + PMD.EOL +
            " public static final int FOO = 2;  " + PMD.EOL +
            "}";


}
