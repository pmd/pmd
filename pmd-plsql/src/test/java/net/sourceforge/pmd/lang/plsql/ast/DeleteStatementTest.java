/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class DeleteStatementTest extends AbstractPLSQLParserTst {

    @Test
    public void parseDeleteStatementExample() {
        ASTInput input = plsql.parseResource("DeleteStatementExample.pls");
        List<ASTDeleteStatement> deleteStatements = input.findDescendantsOfType(ASTDeleteStatement.class);
        Assert.assertEquals(3, deleteStatements.size());

        Assert.assertEquals("product_descriptions", deleteStatements.get(0).getChild(0)
                                                                    .getFirstChildOfType(ASTTableName.class).getImage());
    }
}
