package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.ExcessiveImportsRule;

public class ExcessiveImportsRuleTest extends RuleTst {

    private static final String TEST1 =
    "import java.util.Vector;" + CPD.EOL +
    "import java.util.Vector;" + CPD.EOL +
    "import java.util.Vector;" + CPD.EOL +
    "import java.util.Vector;" + CPD.EOL +
    "import java.util.Vector;" + CPD.EOL +
    "import java.util.Vector;" + CPD.EOL +
    "" + CPD.EOL +
    "public class ExcessiveImports1{}";

    private static final String TEST2 =
    "import java.util.Vector;" + CPD.EOL +
    "" + CPD.EOL +
    "public class ExcessiveImports2{}";

    private Rule rule;

    public void setUp() {
        rule = new ExcessiveImportsRule();
        rule.addProperty("minimum", "5");
    }

    public void testSimpleBad() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }

    public void testSimpleOK() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }


}
