package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.AvoidReassigningParametersRule;
import net.sourceforge.pmd.cpd.CPD;

public class AvoidReassigningParametersRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class AvoidReassigningParameters1 {" + CPD.EOL +
    " private void foo(String bar) {" + CPD.EOL +
    "  bar = \"something else\";" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class AvoidReassigningParameters2 {" + CPD.EOL +
    " private void foo(String bar) {}" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class AvoidReassigningParameters3 {" + CPD.EOL +
    " private int bar;" + CPD.EOL +
    " private void foo(String bar) {" + CPD.EOL +
    "  bar = \"hi\";" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class AvoidReassigningParameters4 {" + CPD.EOL +
    " private int bar;" + CPD.EOL +
    " private void foo(String bar) {" + CPD.EOL +
    "  this.bar = \"hi\";" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class AvoidReassigningParameters5 {" + CPD.EOL +
    "" + CPD.EOL +
    " private class Foo {" + CPD.EOL +
    "  public String bar;" + CPD.EOL +
    " }" + CPD.EOL +
    "" + CPD.EOL +
    " private void foo(String bar) {" + CPD.EOL +
    "  Foo f = new Foo();" + CPD.EOL +
    "  f.bar = bar;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST6 =
    "import java.awt.*;" + CPD.EOL +
    "" + CPD.EOL +
    "public class AvoidReassigningParameters6 {" + CPD.EOL +
    " private void foo(GridBagConstraints gbc) {" + CPD.EOL +
    "  gbc.gridx = 2;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private AvoidReassigningParametersRule rule;

    public void setUp() {
        rule = new AvoidReassigningParametersRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testSimple() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void testNoUsage() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
    public void testInstanceVarSameNameAsParam() throws Throwable {
        runTestFromString(TEST3, 1, rule);
    }
    public void testQualifiedNameInstanceVarSameAsParam() throws Throwable {
        runTestFromString(TEST4, 0, rule);
    }
    public void testQualifiedNameSameAsParam() throws Throwable {
        runTestFromString(TEST5, 0, rule);
    }
    public void testAssignmentToParametersField() throws Throwable {
        runTestFromString(TEST6, 0, rule);
    }
}
