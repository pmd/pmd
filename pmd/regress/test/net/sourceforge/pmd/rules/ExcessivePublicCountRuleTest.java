package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.ExcessivePublicCountRule;
import net.sourceforge.pmd.cpd.CPD;

public class ExcessivePublicCountRuleTest extends RuleTst {

    private ExcessivePublicCountRule rule;

    public void setUp() {
        rule = new ExcessivePublicCountRule();
    }

    public void testSimpleOK() throws Throwable {
        rule.addProperty("minimum", "50");
        runTestFromString(TEST1, 0, rule);
    }

    public void testSimpleBad() throws Throwable {
        rule.addProperty("minimum", "2");
        runTestFromString(TEST2, 1, rule);
    }

    private static final String TEST1 =
    "public class ExcessivePublicCountRule1 {" + CPD.EOL +
    " public int foo;" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class ExcessivePublicCountRule2 {" + CPD.EOL +
    " public int foo;" + CPD.EOL +
    " public int bif;" + CPD.EOL +
    " public int baz;" + CPD.EOL +
    " public int bof;" + CPD.EOL +
    "}";

}
