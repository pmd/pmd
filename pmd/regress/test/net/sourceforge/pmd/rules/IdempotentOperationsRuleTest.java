package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.IdempotentOperationsRule;

public class IdempotentOperationsRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "assignment of a local to itself", 1, new IdempotentOperationsRule()),
           // FIXME
           new TestDescriptor(TEST2, "assignment of one array element to another ", 1, new IdempotentOperationsRule())
       });
    }

    private static final String TEST1 =
    "public class Foo {" + CPD.EOL +
    " private void bar() { " + CPD.EOL +
    "  int x = 2;" + CPD.EOL +
    "  x = x;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + CPD.EOL +
    " private void bar() { " + CPD.EOL +
    "  int[] x = {2,3};" + CPD.EOL +
    "  x[0] = x[1];" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

}
