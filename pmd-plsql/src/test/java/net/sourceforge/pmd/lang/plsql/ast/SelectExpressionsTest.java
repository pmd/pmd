/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class SelectExpressionsTest extends AbstractPLSQLParserTst {

    @Test
    @Ignore
    public void parseSelectExpression() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectExpressions.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }

    @Test
    public void parseSelectCount() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectCount.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }
}
