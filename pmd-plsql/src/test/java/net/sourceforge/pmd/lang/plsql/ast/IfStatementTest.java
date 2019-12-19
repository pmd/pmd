/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class IfStatementTest extends AbstractPLSQLParserTst {

    @Test
    public void parseIfWithElseIf() throws Exception {
        String code = "BEGIN\nIF 1 = 1 THEN null;\nELSIF (2 = 2) THEN null;\nELSE null;\nEND IF;\nEND;\n/\n";
        ASTInput input = plsql.parse(code);
        Assert.assertNotNull(input);
    }
}
