package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ClassCastExceptionWithToArrayTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("basic", "ClassCastExceptionWithToArray");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "bad, toArray() with cast", 1, rule),
           new TestDescriptor(TEST2, "ok, no cast", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  Collection c = new ArrayList();" + PMD.EOL +
    "  c.add(new Integer(1));" + PMD.EOL +
    "  Integer[] a=(Integer [])c.toArray();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  Collection c = new ArrayList();" + PMD.EOL +
    "  c.add(new Integer(1));" + PMD.EOL +
    "  Integer[] a = (Integer [])c.toArray(new Integer[c.size()]);;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}
