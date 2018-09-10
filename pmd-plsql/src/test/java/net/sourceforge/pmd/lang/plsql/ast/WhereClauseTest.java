/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class WhereClauseTest extends AbstractPLSQLParserTst {

    @Test
    @Ignore
    public void testFunctionCall() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("WhereClause.pls"));
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testLikeCondition() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("WhereClauseLike.pls"));
        ASTInput input = parsePLSQL(code);
    }
}
