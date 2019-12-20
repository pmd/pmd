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

public class SelectForUpdateTest extends AbstractPLSQLParserTst {

    @Test
    public void parseSelectForUpdateWait() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectForUpdateWait.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
        Assert.assertEquals(5, input.findDescendantsOfType(ASTForUpdateClause.class).size());
    }

    @Test
    public void parseSelectForUpdate() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectForUpdate.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
        List<ASTForUpdateClause> forUpdateClauses = input.findDescendantsOfType(ASTForUpdateClause.class);
        Assert.assertEquals(2, forUpdateClauses.size());
        Assert.assertEquals(2, forUpdateClauses.get(1).jjtGetNumChildren());
        Assert.assertEquals("e", forUpdateClauses.get(1).jjtGetChild(0).getImage());
        Assert.assertEquals("salary", forUpdateClauses.get(1).jjtGetChild(1).getImage());
    }
}
