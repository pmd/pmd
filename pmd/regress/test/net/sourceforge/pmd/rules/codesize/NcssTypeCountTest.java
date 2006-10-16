/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.codesize;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class NcssTypeCountTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("codesize", "NcssTypeCount");
    }

    public void testAll() {
        rule.addProperty("minimum", "13");
        runTests(new TestDescriptor[]{
                new TestDescriptor(TEST1, "short", 0, rule),
                new TestDescriptor(TEST2, "lots of comments", 0, rule),
                new TestDescriptor(TEST3, "long method", 1, rule),
        });
    }

    public void testChangeMinimum() {
        rule.addProperty("minimum", "15"); // validated this number against NCSS
        runTests(new TestDescriptor[]{
                new TestDescriptor(TEST3, "long method - changed minimum", 0, rule),
        });
    }
    private static final String TEST1 =
        "public class Foo {" + PMD.EOL +
        "    public static void main(String args[]) {" + PMD.EOL +
        "     bar();" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        "    public static void main(String args[]) {" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     bar();" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     foo();" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        "    public static void main(String args[]) {" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     bar();" + PMD.EOL +
        "     bar();" + PMD.EOL +
        "     bar();" + PMD.EOL +
        "     bar();" + PMD.EOL +
        "     bar();" + PMD.EOL +
        "     bar();" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     //nothing to see here" + PMD.EOL +
        "     foo();" + PMD.EOL +
        "     foo();" + PMD.EOL +
        "     foo();" + PMD.EOL +
        "     foo();" + PMD.EOL +
        "     foo();" + PMD.EOL +
        "     foo();" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

}

