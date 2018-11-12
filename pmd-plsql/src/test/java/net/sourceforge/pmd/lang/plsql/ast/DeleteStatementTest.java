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

public class DeleteStatementTest extends AbstractPLSQLParserTst {

    @Test
    public void parseDeleteStatementExample() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("DeleteStatementExample.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        List<ASTDeleteStatement> deleteStatements = input.findDescendantsOfType(ASTDeleteStatement.class);
        Assert.assertEquals(3, deleteStatements.size());

        Assert.assertEquals("product_descriptions", deleteStatements.get(0).jjtGetChild(0)
                .getFirstChildOfType(ASTTableName.class).getImage());
    }
}
