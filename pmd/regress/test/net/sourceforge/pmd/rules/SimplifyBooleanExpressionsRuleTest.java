/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

public class SimplifyBooleanExpressionsRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//Expression/ConditionalAndExpression/InstanceOfExpression[position()>1]/UnaryExpression/PrimaryExpression/PrimaryPrefix/Literal/BooleanLiteral");
    }

    public void testInFieldAssignment() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void testInMethodBody() throws Throwable {
        runTestFromString(TEST2, 1, rule);
    }
    public void testOK() throws Throwable {
        runTestFromString(TEST3, 0, rule);
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private boolean foo = (isFoo() == true);" + PMD.EOL +
    " public boolean isFoo() {return foo;}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  boolean bar = (new String().length() >2) == false;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " boolean bar = true;" + PMD.EOL +
    "}";
}
