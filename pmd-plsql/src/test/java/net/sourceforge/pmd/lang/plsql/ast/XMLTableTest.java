/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;


public class XMLTableTest extends AbstractPLSQLParserTst {

    @Test
    public void testParseXMLTable() {
        ASTInput node = plsql.parseResource("XMLTable.pls");

        List<ASTFunctionCall> functions = node.findDescendantsOfType(ASTFunctionCall.class);
        ASTFunctionCall xmlforest = functions.get(functions.size() - 1);
        Assert.assertEquals("XMLFOREST", xmlforest.getImage());
        Assert.assertEquals("e.employee_id", xmlforest.getChild(1).getImage());
        Assert.assertEquals("foo", xmlforest.getChild(2).getImage());
        Assert.assertTrue(xmlforest.getChild(2) instanceof ASTID);
        Assert.assertEquals("e.last_name", xmlforest.getChild(3).getImage());
        Assert.assertEquals("last_name", xmlforest.getChild(4).getImage());
        Assert.assertTrue(xmlforest.getChild(4) instanceof ASTID);
        Assert.assertEquals("e.salary", xmlforest.getChild(5).getImage());
        Assert.assertEquals(6, xmlforest.getNumChildren());
    }
}
