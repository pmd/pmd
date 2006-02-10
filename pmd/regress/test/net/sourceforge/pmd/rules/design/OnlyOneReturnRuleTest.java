/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class OnlyOneReturnRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("controversial", "OnlyOneReturn");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "two returns", 1, rule),
            new TestDescriptor(TEST2, "one", 0, rule),
            new TestDescriptor(TEST3, "none", 0, rule),
            new TestDescriptor(TEST4, "void", 0, rule),
            new TestDescriptor(TEST5, "finally", 0, rule),
            new TestDescriptor(TEST6, "return inside anonymous inner class", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public int foo(int x) {    " + PMD.EOL +
            "  if (x > 0) {" + PMD.EOL +
            "   return 3;" + PMD.EOL +
            "  }" + PMD.EOL +
            "  return 2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";


    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public int foo(int x) {    " + PMD.EOL +
            "  return 2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public void foo(int x) {      " + PMD.EOL +
            "  int y =2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " public void foo(int x) {      " + PMD.EOL +
            "  if (x>2) {" + PMD.EOL +
            "    return;" + PMD.EOL +
            "  }" + PMD.EOL +
            "  int y =2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " public int foo(int x) {" + PMD.EOL +
            "  try {" + PMD.EOL +
            "   x += 2;" + PMD.EOL +
            "   return x;" + PMD.EOL +
            "  } finally {" + PMD.EOL +
            "    int y;" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST6 =
            "public class Foo {" + PMD.EOL +
            " public int foo() {" + PMD.EOL +
            "  FileFilter f = new FileFilter() {" + PMD.EOL +
            "   public boolean accept(File file) {" + PMD.EOL +
            "    return false;" + PMD.EOL +
            "   }" + PMD.EOL +
            "  };" + PMD.EOL +
            "  return 2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
}
