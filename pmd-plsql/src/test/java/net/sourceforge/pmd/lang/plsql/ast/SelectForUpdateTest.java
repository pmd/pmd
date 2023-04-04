/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class SelectForUpdateTest extends AbstractPLSQLParserTst {

    @Test
    void parseSelectForUpdateWait() {
        ASTInput input = plsql.parseResource("SelectForUpdateWait.pls");
        assertNotNull(input);
        assertEquals(5, input.findDescendantsOfType(ASTForUpdateClause.class).size());
    }

    @Test
    void parseSelectForUpdate() {
        ASTInput input = plsql.parseResource("SelectForUpdate.pls");
        assertNotNull(input);
        List<ASTForUpdateClause> forUpdateClauses = input.findDescendantsOfType(ASTForUpdateClause.class);
        assertEquals(2, forUpdateClauses.size());
        assertEquals(2, forUpdateClauses.get(1).getNumChildren());
        assertEquals("e", forUpdateClauses.get(1).getChild(0).getImage());
        assertEquals("salary", forUpdateClauses.get(1).getChild(1).getImage());
    }
}
