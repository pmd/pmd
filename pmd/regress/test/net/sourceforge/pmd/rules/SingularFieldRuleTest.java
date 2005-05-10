package test.net.sourceforge.pmd.rules;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.PMD;

public class SingularFieldRuleTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/controversial.xml", "SingularField");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "failure case", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "second method uses 'this'", 0, rule),
           new TestDescriptor(TEST4, "skip publics", 0, rule),
           new TestDescriptor(TEST5, "skip statics", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private int x;" + PMD.EOL +
    " int bar(int y) {" + PMD.EOL +
    "  x = y + 5; " + PMD.EOL +
    "  return x;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private int x;" + PMD.EOL +
    " void setX(int x) {" + PMD.EOL +
    "  this.x = x;" + PMD.EOL +
    " }" + PMD.EOL +
    " int getX() {" + PMD.EOL +
    "  return x;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " private int x;" + PMD.EOL +
    " void setX(int x) {" + PMD.EOL +
    "  this.x = x;" + PMD.EOL +
    " }" + PMD.EOL +
    " int getX() {" + PMD.EOL +
    "  return this.x;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " public int x;" + PMD.EOL +
    " int bar(int y) {" + PMD.EOL +
    "  x = y + 5; " + PMD.EOL +
    "  return x;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " private static int x;" + PMD.EOL +
    " int bar(int y) {" + PMD.EOL +
    "  x = y + 5; " + PMD.EOL +
    "  return x;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
