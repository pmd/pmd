/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class XMLElementTest extends AbstractPLSQLParserTst {

    @Test
    void testParseXMLElement() {
        ASTInput input = plsql.parseResource("XMLElement.pls");
        List<ASTXMLElement> xmlelements = input.findDescendantsOfType(ASTXMLElement.class);
        assertEquals(10, xmlelements.size());
        assertEquals("\"Emp\"", xmlelements.get(0).getFirstChildOfType(ASTID.class).getImage());
        assertTrue(xmlelements.get(3).getChild(1) instanceof ASTXMLAttributesClause);
    }
}
