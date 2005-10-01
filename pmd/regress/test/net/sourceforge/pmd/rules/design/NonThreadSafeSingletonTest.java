package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class NonThreadSafeSingletonTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "NonThreadSafeSingleton");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "failure case", 1, rule),
           new TestDescriptor(TEST2, "OK, method is synchronized", 0, rule),
           new TestDescriptor(TEST3, "OK, in synchronized block", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private static List buz;" + PMD.EOL +
    " public static List bar() {" + PMD.EOL +
    "  if (buz == null) buz = new ArrayList();" + PMD.EOL +
    "  return buz;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private static List buz;" + PMD.EOL +
    " public static synchronized List bar() {" + PMD.EOL +
    "  if (buz == null) buz = new ArrayList();" + PMD.EOL +
    "  return buz;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " private static List buz;" + PMD.EOL +
    " public static List bar() {" + PMD.EOL +
    "  synchronized (baz) {" + PMD.EOL +
    "   if (buz == null) buz = new ArrayList();" + PMD.EOL +
    "   return buz;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
