/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class EmptyCatchBlockRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/basic.xml", "EmptyCatchBlock");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "no catch with nested catch in finally", 1, rule),
           new TestDescriptor(TEST4, "multiple catch blocks", 2, rule),
           new TestDescriptor(TEST5, "empty try with finally", 0, rule),
           new TestDescriptor(TEST6, "InterruptedException is OK", 0, rule),
           new TestDescriptor(TEST7, "CloneNotSupportedException is OK", 0, rule),
       });
    }

    public static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  try {} catch (Exception e) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  try {} catch (RuntimeException e) {e.getMessage();}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  try {} finally { " + PMD.EOL +
    "   try {" + PMD.EOL +
    "    int x =2;" + PMD.EOL +
    "   } catch (Exception e) {}" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  try {" + PMD.EOL +
    "  } catch (Exception e) {" + PMD.EOL +
    "  } catch (Throwable t) {" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  try {" + PMD.EOL +
    "  } catch (Exception e) {" + PMD.EOL +
    "   ;" + PMD.EOL +
    "  } finally {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  try {" + PMD.EOL +
    "  } catch (InterruptedException e) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST7 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  try {" + PMD.EOL +
    "  } catch (CloneNotSupportedException e) {}" + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}

