package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.UnusedFormalParameterRule;
import net.sourceforge.pmd.cpd.CPD;

public class UnusedFormalParameterRuleTest extends RuleTst {

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

    private UnusedFormalParameterRule rule;

    public void setUp() {
        rule = new UnusedFormalParameterRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testOneParam() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void testFullyQualified() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
    public void testOneParamWithMethodCall() throws Throwable {
        runTestFromString(TEST3, 0, rule);
    }
    public void testInterface() throws Throwable {
        runTestFromString(TEST4, 0, rule);
    }
}
