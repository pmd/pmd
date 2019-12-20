/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class InsertIntoClauseTest extends AbstractPLSQLParserTst {

    @Test
    public void parseInsertInto() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("InsertIntoClause.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }

    @Test
    public void parseInsertIntoReturning() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("InsertIntoClauseReturning.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }

    @Test
    public void parseInsertIntoWithRecord() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("InsertIntoClauseRecord.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }
}
