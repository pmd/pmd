package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class FinalizeShouldBeProtectedRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//MethodDeclaration[@Protected=\"false\"]/MethodDeclarator[@Image=\"finalize\"][not(FormalParameters/*)]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "public finalize", 1, rule),
           new TestDescriptor(TEST2, "finalize with some params", 0, rule),
           new TestDescriptor(TEST3, "legitimate overriding", 0, rule)
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public void finalize() {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " public void finalize(int x) {}" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " protected void finalize() {}" + PMD.EOL +
    "}";

}
