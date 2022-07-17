/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp;

import org.junit.Test;

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

}
