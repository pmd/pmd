package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.AvoidReassigningParametersRule;

public class AvoidReassigningParametersRuleTest extends RuleTst {

    public static final String TEST1 =
    "public class AvoidReassigningParameters1 {" + PMD.EOL +
    " private void foo(String bar) {" + PMD.EOL +
    "  bar = \"something else\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST2 =
    "public class AvoidReassigningParameters2 {" + PMD.EOL +
    " private void foo(String bar) {}" + PMD.EOL +
    "}";

    public static final String TEST3 =
    "public class AvoidReassigningParameters3 {" + PMD.EOL +
    " private int bar;" + PMD.EOL +
    " private void foo(String bar) {" + PMD.EOL +
    "  bar = \"hi\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST4 =
    "public class AvoidReassigningParameters4 {" + PMD.EOL +
    " private int bar;" + PMD.EOL +
    " private void foo(String bar) {" + PMD.EOL +
    "  this.bar = \"hi\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST5 =
    "public class AvoidReassigningParameters5 {" + PMD.EOL +
    "" + PMD.EOL +
    " private class Foo {" + PMD.EOL +
    "  public String bar;" + PMD.EOL +
    " }" + PMD.EOL +
    "" + PMD.EOL +
    " private void foo(String bar) {" + PMD.EOL +
    "  Foo f = new Foo();" + PMD.EOL +
    "  f.bar = bar;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST6 =
    "import java.awt.*;" + PMD.EOL +
    "" + PMD.EOL +
    "public class AvoidReassigningParameters6 {" + PMD.EOL +
    " private void foo(GridBagConstraints gbc) {" + PMD.EOL +
    "  gbc.gridx = 2;" + PMD.EOL +
    " }" + PMD.EOL +
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
