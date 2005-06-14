package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UseArrayListInsteadOfVectorTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/optimizations.xml", "UseArrayListInsteadOfVector");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST0, "TEST0", 0, rule),
               new TestDescriptor(TEST1, "TEST1", 1, rule),
               new TestDescriptor(TEST2, "TEST2", 1, rule),
               new TestDescriptor(TEST3, "TEST3", 1, rule),
       });
    }


    private static final String TEST0 =
    "public class Bar {" + PMD.EOL +
    " void x() {" + PMD.EOL + 
    "  List v = new ArrayList(); " + PMD.EOL +
    " }" + PMD.EOL +
    "}";
    
    private static final String TEST1 =
    "public class Bar {" + PMD.EOL +
    " void x() {" + PMD.EOL +
    "  Vector v = new Vector(); " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Bar {" + PMD.EOL +
    " Vector v = new Vector(); " + PMD.EOL +
    " void x() {}" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Bar {" + PMD.EOL +
    " List v = new Vector(); " + PMD.EOL +
    " void x() {}" + PMD.EOL +
    "}";

}
