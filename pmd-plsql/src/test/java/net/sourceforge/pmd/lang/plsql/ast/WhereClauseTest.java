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

public class WhereClauseTest extends AbstractPLSQLParserTst {

    @Test
    public void testFunctionCall() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("WhereClauseFunctionCall.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        List<ASTSelectIntoStatement> selectStatements = input.findDescendantsOfType(ASTSelectIntoStatement.class);
        Assert.assertEquals(4, selectStatements.size());

        ASTFunctionCall functionCall = selectStatements.get(0).getFirstDescendantOfType(ASTFunctionCall.class);
        Assert.assertEquals("UPPER", functionCall.getImage());

        ASTFunctionCall functionCall2 = selectStatements.get(2).getFirstDescendantOfType(ASTFunctionCall.class);
        Assert.assertEquals("utils.get_colname", functionCall2.getImage());
    }

    @Test
    public void testLikeCondition() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("WhereClauseLike.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testNullCondition() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("WhereClauseIsNull.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testBetweenCondition() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("WhereClauseBetween.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testInCondition() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("WhereClauseIn.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testIsOfTypeCondition() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("WhereClauseIsOfType.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testConcatenationOperator() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("WhereClauseConcatenation.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testExistsCondition() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("WhereClauseExists.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testMultisetCondition() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("WhereClauseMultiset.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testRegexpLikeCondition() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("WhereClauseRegexpLike.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        List<ASTRegexpLikeCondition> regexps = input.findDescendantsOfType(ASTRegexpLikeCondition.class);
        Assert.assertEquals(3, regexps.size());
        Assert.assertEquals("last_name", regexps.get(1).getSourceChar().getImage());
        Assert.assertEquals("'([aeiou])\\1'", regexps.get(1).getPattern().getImage());
        Assert.assertEquals("'i'", regexps.get(1).getMatchParam());
    }

    @Test
    public void testSubqueries() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("WhereClauseSubqueries.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }
}
