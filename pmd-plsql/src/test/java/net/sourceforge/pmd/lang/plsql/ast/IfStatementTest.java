/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class IfStatementTest extends AbstractPLSQLParserTst {

    @Test
    void parseIfWithElseIf() throws Exception {
        String code = "BEGIN\nIF 1 = 1 THEN null;\nELSIF (2 = 2) THEN null;\nELSE null;\nEND IF;\nEND;\n/\n";
        ASTInput input = plsql.parse(code);
        assertNotNull(input);
    }
}
