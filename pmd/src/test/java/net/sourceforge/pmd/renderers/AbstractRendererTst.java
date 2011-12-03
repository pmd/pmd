/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.testframework.RuleTst;

import org.junit.Test;


public abstract class AbstractRendererTst extends RuleTst {

    private static class FooRule extends AbstractJavaRule {
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
                addViolation(ctx, c.jjtGetChild(0));
            }
            return ctx;
        }
    }

    public abstract Renderer getRenderer();

    public abstract String getExpected();

    public abstract String getExpectedEmpty();

    public abstract String getExpectedMultiple();

    public String getExpectedError(ProcessingError error) {
        return "";
    }

    @Test(expected = NullPointerException.class)
    public void testNullPassedIn() throws Throwable {
	ReportTest.render(getRenderer(), null);
    }

    @Test
    public void testRenderer() throws Throwable {
        Report rep = new Report();
        runTestFromString(TEST1, new FooRule(), rep);
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(getExpected(), actual);
    }

    @Test
    public void testRendererEmpty() throws Throwable {
        Report rep = new Report();
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(getExpectedEmpty(), actual);
    }

    @Test
    public void testRendererMultiple() throws Throwable {
        Report rep = new Report();
        runTestFromString(TEST1, new FooRule2(), rep);
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(getExpectedMultiple(), actual);
    }

    @Test
    public void testError() throws Throwable {
        Report rep = new Report();
        Report.ProcessingError err = new Report.ProcessingError("Error", "file");
        rep.addError(err);
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(getExpectedError(err), actual);
    }

    private static final String TEST1 = "public class Foo {}" + PMD.EOL;
}
