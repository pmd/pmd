/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class JumbledIncrementerRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/basic.xml", "JumbledIncrementer");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "", 1, rule),
           new TestDescriptor(TEST2, "", 0, rule),
           new TestDescriptor(TEST3, "", 0, rule),
           new TestDescriptor(TEST4, "using outer loop incrementor as array index is OK", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void foo() { " + PMD.EOL +
    "  for (int i = 0; i < 10; i++) { " + PMD.EOL +
    "   for (int k = 0; k < 20; i++) { " + PMD.EOL +
    "    int x = 2; " + PMD.EOL +
    "   } " + PMD.EOL +
    "  } " + PMD.EOL +
    " } " + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void foo() { " + PMD.EOL +
    "  for (int i = 0; i < 10; i++) { " + PMD.EOL +
    "   for (int k = 0; k < 20; k++) { " + PMD.EOL +
    "    int x = 2; " + PMD.EOL +
    "   } " + PMD.EOL +
    "  } " + PMD.EOL +
    " } " + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " void foo() { " + PMD.EOL +
    "  for (int i=0; i<5; ) {" + PMD.EOL +
    "   i++;" + PMD.EOL +
    "  }" + PMD.EOL +
    "  for (int i=0;;) {" + PMD.EOL +
    "   if (i<5) {" + PMD.EOL +
    "    break;" + PMD.EOL +
    "   }" + PMD.EOL +
    "   i++;" + PMD.EOL +
    "  }" + PMD.EOL +
    "  for (;;) {" + PMD.EOL +
    "   int x =5;" + PMD.EOL +
    "  }" + PMD.EOL +
    "  for (int i=0; i<5;i++) ;" + PMD.EOL +
    "  for (int i=0; i<5;i++) " + PMD.EOL +
    "   foo();" + PMD.EOL +
    " } " + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " void foo() { " + PMD.EOL +
    "  for (int i = 0; i < 10; i++) { " + PMD.EOL +
    "   for (int k = 0; k < 20; j[i]++) { " + PMD.EOL +
    "    int x = 2; " + PMD.EOL +
    "   } " + PMD.EOL +
    "  } " + PMD.EOL +
    " } " + PMD.EOL +
    "}";

}
