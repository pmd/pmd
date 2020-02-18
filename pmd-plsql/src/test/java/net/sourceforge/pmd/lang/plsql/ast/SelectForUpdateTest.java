/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class SelectForUpdateTest extends AbstractPLSQLParserTst {

    @Test
    public void parseSelectForUpdateWait() {
        ASTInput input = plsql.parseResource("SelectForUpdateWait.pls");
        Assert.assertNotNull(input);
        Assert.assertEquals(5, input.findDescendantsOfType(ASTForUpdateClause.class).size());
    }

    @Test
    public void parseSelectForUpdate() {
        ASTInput input = plsql.parseResource("SelectForUpdate.pls");
        Assert.assertNotNull(input);
        List<ASTForUpdateClause> forUpdateClauses = input.findDescendantsOfType(ASTForUpdateClause.class);
        Assert.assertEquals(2, forUpdateClauses.size());
        Assert.assertEquals(2, forUpdateClauses.get(1).getNumChildren());
        Assert.assertEquals("e", forUpdateClauses.get(1).getChild(0).getImage());
        Assert.assertEquals("salary", forUpdateClauses.get(1).getChild(1).getImage());
    }
}
