package test.net.sourceforge.pmd.rules;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.PMD;

public class MisplacedNullCheckTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("basic", "MisplacedNullCheck");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "null check after method invocation", 1, rule),
           new TestDescriptor(TEST2, "null check after nested method invocation", 1, rule),
           new TestDescriptor(TEST3, "null check before nested method invocation", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
	" void bar() {" + PMD.EOL +
	"  if (a.equals(baz) && a!=null) {}" + PMD.EOL +
	" }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
	" void bar() {" + PMD.EOL +
	"  if (a.equals(baz.foo()) && baz != null) {}" + PMD.EOL +
	" }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
	" void bar() {" + PMD.EOL +
	"  if (a != null && a.equals(foo())) {}" + PMD.EOL +
	" }" + PMD.EOL +
    "}";

}
