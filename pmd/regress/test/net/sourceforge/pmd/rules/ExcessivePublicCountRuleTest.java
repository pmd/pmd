package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.ExcessivePublicCountRule;

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
    "public class ExcessivePublicCountRule1 {" + PMD.EOL +
    " public int foo;" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class ExcessivePublicCountRule2 {" + PMD.EOL +
    " public int foo;" + PMD.EOL +
    " public int bif;" + PMD.EOL +
    " public int baz;" + PMD.EOL +
    " public int bof;" + PMD.EOL +
    "}";

}
