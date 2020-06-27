/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp;

import java.io.File;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.jsp.ast.AbstractJspNodesTst;

/**
 * Unit test for JSP parsing.
 *
 */
public class JspParserTest extends AbstractJspNodesTst {

    /**
     * Verifies bug #939 Jsp parser fails on $
     */
    @Test
    public void testParseDollar() {
    }

    @Test
    public void testParseELAttribute() {
        jsp.parse("<div ${something ? 'class=\"red\"' : ''}> Div content here.</div>");
    }

    @Test
    public void testParseELAttributeValue() {
        jsp.parse("<div class=\"${something == 0 ? 'zero_something' : something == 1 ? 'one_something' : 'other_something'}\">Div content here.</div>");
    }

    /**
     * Verifies bug #311 Jsp parser fails on boolean attribute
     */
    @Test
    public void testParseBooleanAttribute() {
        jsp.parse("<label><input type='checkbox' checked name=cheese disabled=''> Cheese</label>");
    }

    @Test
    public void testParseJsp() {
        testInternalJspFile(Paths.get("sample.jsp").toFile());
        testInternalJspFile(Paths.get("sample.jspx").toFile());
    }

    @Test
    public void testParseTag() {
        testInternalJspFile(Paths.get("sample.tag").toFile());
    }

    @Test(expected = AssertionError.class)
    public void testParseWrong() {
        testInternalJspFile(Paths.get("sample.xxx").toFile());
    }

    private void testInternalJspFile(File jspFile) {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(jspFile);
        Assert.assertEquals("LanguageVersion must be JSP!",
                jsp.getLanguage().getDefaultVersion(), languageVersion);
    }

}
