package test.net.sourceforge.pmd.rules.finalize;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class FinalizeDoesNotCallSuperFinalizeRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/finalizers.xml", "FinalizeDoesNotCallSuperFinalize");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "bad", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public void finalize() {" + PMD.EOL +
    "  super.finalize();" + PMD.EOL +
    "  int x = 2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " public void finalize() {" + PMD.EOL +
    "  int x = 2;" + PMD.EOL +
    "  super.finalize();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

/*
TODO
This should handle a call to super.finalize() within a finally block, e.g.

protected void finalize()
{
   try { //  do something
    }
   finally
   {
      super.finalize(); // this is OK and should not be flagged
   }
}
*/
}
