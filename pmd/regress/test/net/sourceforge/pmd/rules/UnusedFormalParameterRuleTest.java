package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.UnusedFormalParameterRule;

public class UnusedFormalParameterRuleTest extends SimpleAggregatorTst {

    private UnusedFormalParameterRule rule;

    public void setUp() {
        rule = new UnusedFormalParameterRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "one parameter", 1, rule),
           new TestDescriptor(TEST2, "fully qualified parameter", 0, rule),
           new TestDescriptor(TEST3, "one parameter with a method call", 0, rule),
           new TestDescriptor(TEST4, "interface", 0, rule)
       });
    }

    private static final String TEST1 =
    "class UnusedFormalParam1 {" + CPD.EOL +
    "    private void testMethod(String param1) {" + CPD.EOL +
    "        //System.out.println(param1);" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "class UnusedFormalParam2 {" + CPD.EOL +
    "    private void foo (String s) " + CPD.EOL +
    "    {String str = s.toString();}" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "class UnusedFormalParam3 {" + CPD.EOL +
    "    private void t1(String s) {" + CPD.EOL +
    "        s.toString();" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public interface UnusedFormalParam4 {" + CPD.EOL +
    " public void foo(String bar);" + CPD.EOL +
    "}";

}
