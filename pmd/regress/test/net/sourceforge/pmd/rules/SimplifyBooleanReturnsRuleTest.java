/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.SimplifyBooleanReturnsRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class SimplifyBooleanReturnsRuleTest extends SimpleAggregatorTst {


    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "bad", 1, new SimplifyBooleanReturnsRule()),
           new TestDescriptor(TEST2, "bad", 1, new SimplifyBooleanReturnsRule()),
           new TestDescriptor(TEST3, "ok", 0, new SimplifyBooleanReturnsRule()),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public void foo() {   " + PMD.EOL +
    "  if (true) {" + PMD.EOL +
    "   return true;" + PMD.EOL +
    "  } else {" + PMD.EOL +
    "   return false;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " public boolean foo() {        " + PMD.EOL +
    "  if (true) " + PMD.EOL +
    "   return true;" + PMD.EOL +
    "   else " + PMD.EOL +
    "  return false;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " public Object foo() { " + PMD.EOL +
    "  if (!true) {" + PMD.EOL +
    "   return null;" + PMD.EOL +
    "  } else {}" + PMD.EOL +
    "  return null;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
