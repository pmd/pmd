package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class UnusedModifierRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//InterfaceDeclaration//MethodDeclaration[@Public='true' or @Abstract = 'true']");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "wasted 'public'", 1, rule),
           new TestDescriptor(TEST2, "class, no problem", 0, rule),
           new TestDescriptor(TEST3, "wasted 'abstract'", 1, rule),
           new TestDescriptor(TEST4, "all is well", 0, rule)
       });
    }

    private static final String TEST1 =
    "public interface Foo {" + CPD.EOL +
    " public void bar();" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public abstract class Foo {" + CPD.EOL +
    " public abstract void bar();" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public interface Foo {" + CPD.EOL +
    " abstract void bar();" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public interface Foo {" + CPD.EOL +
    " void bar();" + CPD.EOL +
    "}";

}
