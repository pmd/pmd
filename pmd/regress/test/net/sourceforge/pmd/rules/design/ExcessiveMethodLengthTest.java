/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.rules.design.LongMethodRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ExcessiveMethodLengthTest extends SimpleAggregatorTst  {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/codesize.xml", "ExcessiveMethodLength");
        rule.addProperty("minimum", "10");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "short", 0, rule),
           new TestDescriptor(TEST2, "long", 1, rule),
           new TestDescriptor(TEST3, "not quite long", 0, rule),
           new TestDescriptor(TEST4, "long", 1, rule),
       });
    }

    public void testReallyLongMethodWithLongerRange() throws Throwable {
        LongMethodRule IUT = new LongMethodRule();
        IUT.addProperty("minimum", "20");
        runTestFromString(TEST2, 0, IUT);
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
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
    "	  bar();" + PMD.EOL +
    "    } // 11 lines - violation" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "    } // 9 lines - Not a violation" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
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
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "	  bar();" + PMD.EOL +
    "    } // > 10 lines - Not a violation" + PMD.EOL +
    "}";
}

