package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class UnusedModifierRuleTest extends RuleTst {

    private static final String TEST1 =
    "public interface UnusedModifier1 {" + CPD.EOL +
    " public void bar();" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public abstract class UnusedModifier2 {" + CPD.EOL +
    " public abstract void bar();" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public interface UnusedModifier3 {" + CPD.EOL +
    " abstract void bar();" + CPD.EOL +
    "}";

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//InterfaceDeclaration//MethodDeclaration[@Public='true' or @Abstract = 'true']");
    }

    public void testAbstract() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void testAbstractClass() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
    public void testPublicAndAbstract() throws Throwable {
        runTestFromString(TEST3, 1, rule);
    }
}
