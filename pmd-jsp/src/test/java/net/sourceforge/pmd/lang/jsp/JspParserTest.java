/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Unit test for JSP parsing.
 *
 */
public class JspParserTest {

    /**
     * Verifies bug #939 Jsp parser fails on $
     */
    @Test
    public void testParseDollar() {
        Node node = parse(
                "<span class=\"CostUnit\">$</span><span class=\"CostMain\">129</span><span class=\"CostFrac\">.00</span>");
        Assert.assertNotNull(node);
    }

    @Test
    public void testParseELAttribute() {
        Node node = parse(
                "<div ${something ? 'class=\"red\"' : ''}> Div content here.</div>");
        Assert.assertNotNull(node);
    }

    @Test
    public void testParseELAttributeValue() {
        Node node = parse(
                "<div class=\"${something == 0 ? 'zero_something' : something == 1 ? 'one_something' : 'other_something'}\">Div content here.</div>");
        Assert.assertNotNull(node);
    }

    /**
     * Verifies bug #311 Jsp parser fails on boolean attribute
     */
    @Test
    public void testParseBooleanAttribute() {
        Node node = parse(
                "<label><input type='checkbox' checked name=cheese disabled=''> Cheese</label>");
        Assert.assertNotNull(node);
    }

    private Node parse(String code) {
        LanguageVersionHandler jspLang = LanguageRegistry.getLanguage(JspLanguageModule.NAME).getDefaultVersion()
                .getLanguageVersionHandler();
        Parser parser = jspLang.getParser(jspLang.getDefaultParserOptions());
        Node node = parser.parse(null, new StringReader(code));
        return node;
    }
}
