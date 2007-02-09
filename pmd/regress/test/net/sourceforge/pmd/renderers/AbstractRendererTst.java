/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.renderers.AbstractRenderer;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.RuleTst;

public abstract class AbstractRendererTst extends RuleTst {

    private static class FooRule extends AbstractRule {
        public Object visit(ASTClassOrInterfaceDeclaration c, Object ctx) {
            if (c.getImage().equals("Foo"))
                addViolation(ctx, c);
            return ctx;
        }
        public String getMessage() { return "msg";  }
        public String getName() { return "Foo"; }
        public String getRuleSetName() { return "RuleSet"; }
        public String getDescription() { return "desc"; }
    }

    private static class FooRule2 extends FooRule {
        public Object visit(ASTClassOrInterfaceDeclaration c, Object ctx) {
            if (c.getImage().equals("Foo")) {
                addViolation(ctx, c);
                addViolation(ctx, (SimpleNode) c.jjtGetChild(0));
            }
            return ctx;
        }
    }

    public abstract AbstractRenderer getRenderer();

    public abstract String getExpected();

    public abstract String getExpectedEmpty();

    public abstract String getExpectedMultiple();

    public String getExpectedError(ProcessingError error) {
        return "";
    }

    @Test(expected = NullPointerException.class)
    public void testNullPassedIn() {
        getRenderer().render(null);
    }

    @Test
    public void testRenderer() throws Throwable {
        Report rep = new Report();
        runTestFromString(TEST1, new FooRule(), rep);
        String actual = getRenderer().render(rep);
        assertEquals(getExpected(), actual);
    }

    @Test
    public void testRendererEmpty() throws Throwable {
        Report rep = new Report();
        String actual = getRenderer().render(rep);
        assertEquals(getExpectedEmpty(), actual);
    }

    @Test
    public void testRendererMultiple() throws Throwable {
        Report rep = new Report();
        runTestFromString(TEST1, new FooRule2(), rep);
        String actual = getRenderer().render(rep);
        assertEquals(getExpectedMultiple(), actual);
    }

    @Test
    public void testError() throws Throwable {
        Report rep = new Report();
        Report.ProcessingError err = new Report.ProcessingError("Error", "file");
        rep.addError(err);
        String actual = getRenderer().render(rep);
        assertEquals(getExpectedError(err), actual);
    }

    private static final String TEST1 = "public class Foo {}" + PMD.EOL;
}
