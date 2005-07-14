/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.rules.design.LongClassRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class LongClassRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("codesize", "ExcessiveClassLength");
        rule.addProperty("minimum", "10");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST0, "short", 0, rule),
           new TestDescriptor(TEST1, "long", 1, rule),
       });
    }

    public void testLongClassWithLongerTest() throws Throwable {
        LongClassRule IUT = new LongClassRule();
        IUT.addProperty("minimum", "2000");
        runTestFromString(TEST1, 0, IUT);
    }

    private static final String TEST0 =
    "public class Foo {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "	  int x;" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    "    public void bar() {" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";
}

