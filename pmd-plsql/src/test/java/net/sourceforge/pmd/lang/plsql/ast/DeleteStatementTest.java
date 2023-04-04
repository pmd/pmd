/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class DeleteStatementTest extends AbstractPLSQLParserTst {

    @Test
    void parseDeleteStatementExample() {
        ASTInput input = plsql.parseResource("DeleteStatementExample.pls");
        List<ASTDeleteStatement> deleteStatements = input.findDescendantsOfType(ASTDeleteStatement.class);
        assertEquals(3, deleteStatements.size());

        assertEquals("product_descriptions", deleteStatements.get(0).getChild(0)
                                                                    .getFirstChildOfType(ASTTableName.class).getImage());
    }
}
