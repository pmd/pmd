/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class UpdateStatementTest extends AbstractPLSQLParserTst {
    @Test
    public void parseUpdateStatementExample() {
        String code = loadTestResource("UpdateStatementExample.pls");
        ASTInput input = parsePLSQL(code);
    }
}
