/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.renderers;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.renderers.TextPadRenderer;
import test.net.sourceforge.pmd.testframework.MockRule;

public class TextPadRendererTest extends TestCase  {

    public void testNullPassedIn() {
        try  {
            (new TextPadRenderer()).render(null);
            fail("Providing a render(null) should throw an npx");
        }  catch(NullPointerException npx)  {
            // cool
        }
    }

    public void testRenderer()  {
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("Foo.java");
        Report rep = new Report();
        rep.addRuleViolation(new RuleViolation(new MockRule("DontImportJavaLang", "Avoid importing anything from the package 'java.lang'", "Avoid importing anything from the package 'java.lang'"), 3,ctx));
        String actual = (new TextPadRenderer()).render(rep);
        String expected = PMD.EOL + "Foo.java(3,  DontImportJavaLang):  Avoid importing anything from the package 'java.lang'" ;
        assertEquals(expected, actual);
    }
}









