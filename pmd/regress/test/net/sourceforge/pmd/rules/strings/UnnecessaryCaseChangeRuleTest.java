package test.net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnnecessaryCaseChangeRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("rulesets/strings.xml", "UnnecessaryCaseChange");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "failure case with toUpperCase().equals()", 1, rule),
           new TestDescriptor(TEST2, "failure case with toLowerCase().equals()", 1, rule),
           new TestDescriptor(TEST3, "failure case with toUpperCase().equalsIgnoreCase()", 1, rule),
           //new TestDescriptor(TEST4, "failure case with array", 1, rule),
       });
    }

   private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private boolean baz(String buz) {" + PMD.EOL +
    "  return foo.toUpperCase().equals(\"foo\");" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

   private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private boolean baz(String buz) {" + PMD.EOL +
    "  return foo.toLowerCase().equals(\"foo\");" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

   private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " private boolean baz(String buz) {" + PMD.EOL +
    "  return foo.toUpperCase().equalsIgnoreCase(\"foo\");" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

/*
   private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " private boolean baz(String[] buz) {" + PMD.EOL +
    "  return buz[2].toUpperCase().equalsIgnoreCase(\"foo\");" + PMD.EOL +
    " }" + PMD.EOL +
    "}";
*/

}
