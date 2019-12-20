/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class XMLElementTest extends AbstractPLSQLParserTst {

    @Test
    public void testParseXMLElement() throws Exception {
        ASTInput input = parsePLSQL(IOUtils.toString(this.getClass().getResourceAsStream("XMLElement.pls"),
                StandardCharsets.UTF_8));
        Assert.assertNotNull(input);
        List<ASTXMLElement> xmlelements = input.findDescendantsOfType(ASTXMLElement.class);
        Assert.assertEquals(10, xmlelements.size());
        Assert.assertEquals("\"Emp\"", xmlelements.get(0).getFirstChildOfType(ASTID.class).getImage());
        Assert.assertTrue(xmlelements.get(3).jjtGetChild(1) instanceof ASTXMLAttributesClause);
    }
}
