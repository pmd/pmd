package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.CouplingBetweenObjectsRule;

public class CouplingBetweenObjectsRuleTest extends RuleTst {

    private static final String TEST1 =
    "import java.util.*;" + CPD.EOL +
    "" + CPD.EOL +
    "public class CouplingBetweenObjects1 {" + CPD.EOL +
    " public List foo() {return null;}" + CPD.EOL +
    " public ArrayList foo() {return null;}" + CPD.EOL +
    " public Vector foo() {return null;}" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class CouplingBetweenObjects2 {" + CPD.EOL +
    "}";


    private Rule rule;

    public void setUp() {
        rule = new CouplingBetweenObjectsRule();
        rule.addProperty("threshold", "2");
    }

    public void testSimpleBad() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }

    public void testSimpleOK() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
}
