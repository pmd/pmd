package test.net.sourceforge.pmd.rules;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.rules.SuspiciousOctalEscapeRule;
import net.sourceforge.pmd.PMD;

public class SuspiciousOctalEscapeRuleTest extends SimpleAggregatorTst {
    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "ok use of octal", 0, new SuspiciousOctalEscapeRule()),
           new TestDescriptor(TEST2, "should be flagged", 1, new SuspiciousOctalEscapeRule()),
       });
    }

   private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  int x = \128;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  System.out.println(\"foo = \\128\");" + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}
