/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class ExecuteImmediateTest extends AbstractPLSQLParserTst {

    @Test
    public void parseExecuteImmediate1047a() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("ExecuteImmediate1047a.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }

    @Test
    public void parseExecuteImmediate1047b() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("ExecuteImmediate1047b.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }
}
