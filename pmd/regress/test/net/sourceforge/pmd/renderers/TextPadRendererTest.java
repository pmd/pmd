/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.renderers.TextPadRenderer;
import test.net.sourceforge.pmd.testframework.RuleTst;

public class TextPadRendererTest extends RuleTst {

    private static class FooRule extends AbstractRule {
        public Object visit(ASTClassOrInterfaceDeclaration c, Object ctx) {
            if (c.getImage().equals("Foo")) addViolation(ctx, c);
            return ctx;
        }

        public String getMessage() {
            return "msg";
        }

        public String getName() {
            return "Foo";
        }

        public String getRuleSetName() {
            return "RuleSet";
        }

        public String getDescription() {
            return "desc";
        }
    }


    public void testNullPassedIn() {
        try {
            (new TextPadRenderer()).render(null);
            fail("Providing a render(null) should throw an npx");
        } catch (NullPointerException npx) {
            // cool
        }
    }

    public void testRenderer() throws Throwable {
        Report rep = new Report();
        runTestFromString(TEST1, new FooRule(), rep);
        String actual = (new TextPadRenderer()).render(rep);
        String expected = PMD.EOL + "n/a(1,  Foo):  msg";
        assertEquals(expected, actual);
    }

    private static final String TEST1 =
            "public class Foo {}" + PMD.EOL;
}









