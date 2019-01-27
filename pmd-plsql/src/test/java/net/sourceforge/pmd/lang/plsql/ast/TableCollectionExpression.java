/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class TableCollectionExpression extends AbstractPLSQLParserTst {

    @Test
    public void testExamples() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("TableCollectionExpressionExamples.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }

    @Test
    public void testIssue1526() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("TableCollectionExpressionIssue1526.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }
}
