package test.net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.PMD;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class UnnecessaryCaseChangeRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("rulesets/strings.xml", "UnnecessaryCaseChange");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "failure case with String.toUpperCase().equals()", 1, rule),
           new TestDescriptor(TEST2, "failure case with String.toLowerCase().equals()", 1, rule),
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

}
