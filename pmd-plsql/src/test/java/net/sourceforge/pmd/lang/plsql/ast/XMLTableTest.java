/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class XMLTableTest extends AbstractPLSQLParserTst {

    @Test
    void testParseXMLTable() {
        ASTInput node = plsql.parseResource("XMLTable.pls");

        List<ASTFunctionCall> functions = node.findDescendantsOfType(ASTFunctionCall.class);
        ASTFunctionCall xmlforest = functions.get(functions.size() - 1);
        assertEquals("XMLFOREST", xmlforest.getImage());
        assertEquals("e.employee_id", xmlforest.getChild(1).getImage());
        assertEquals("foo", xmlforest.getChild(2).getImage());
        assertTrue(xmlforest.getChild(2) instanceof ASTID);
        assertEquals("e.last_name", xmlforest.getChild(3).getImage());
        assertEquals("last_name", xmlforest.getChild(4).getImage());
        assertTrue(xmlforest.getChild(4) instanceof ASTID);
        assertEquals("e.salary", xmlforest.getChild(5).getImage());
        assertEquals(6, xmlforest.getNumChildren());
    }
}
