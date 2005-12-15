package test.net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UseArraysAsListTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("optimizations", "UseArraysAsList");
    }

    // FIXME should be able to catch case where Integer[] is passed
    // as an argument... but may need to rewrite in Java for that.
    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST1, "failure case", 1, rule),
               new TestDescriptor(TEST2, "adding first element repeatedly", 0, rule),
               new TestDescriptor(TEST3, "inside conditional", 0, rule),
               new TestDescriptor(TEST4, "adding new object", 0, rule),
               new TestDescriptor(TEST5, "calling method", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Bar {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  Integer[] ints = new Integer(10); " + PMD.EOL +
    "  List l= new ArrayList(10); " + PMD.EOL +
    "  for (int i=0; i< 100; i++) { " + PMD.EOL +
    "   l.add(ints[i]); " + PMD.EOL +
    "  } " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Bar {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  Integer[] ints = new Integer(10); " + PMD.EOL +
    "  List l= new ArrayList(10); " + PMD.EOL +
    "  for (int i=0; i< 100; i++) { " + PMD.EOL +
    "   l.add(ints[1]); " + PMD.EOL +
    "  } " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Bar {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  Integer[] ints = new Integer(10); " + PMD.EOL +
    "  List l= new ArrayList(10); " + PMD.EOL +
    "  for (int i=0; i< 100; i++) { " + PMD.EOL +
    "   if (y > 10) { l.add(ints[1]);} " + PMD.EOL +
    "  } " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Bar {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  Integer[] ints = new Integer(10); " + PMD.EOL +
    "  List l= new ArrayList(10); " + PMD.EOL +
    "  for (int i=0; i< 100; i++) { " + PMD.EOL +
    "   l.add(new Integer(i+1)); " + PMD.EOL +
    "  } " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Bar {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  Integer[] ints = new Integer(10); " + PMD.EOL +
    "  List l= new ArrayList(10); " + PMD.EOL +
    "  for (int i=0; i< 100; i++) { " + PMD.EOL +
    "   l.add(String.valueOf(i)); " + PMD.EOL +
    "  } " + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}
