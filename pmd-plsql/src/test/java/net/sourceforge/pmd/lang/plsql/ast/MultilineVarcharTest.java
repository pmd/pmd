/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class MultilineVarcharTest extends AbstractPLSQLParserTst {

    @Test
    public void parseMultilineVarchar() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("MultilineVarchar.sql"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }
}
