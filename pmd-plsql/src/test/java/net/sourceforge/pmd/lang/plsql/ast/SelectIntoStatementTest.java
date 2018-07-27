/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class SelectIntoStatementTest extends AbstractPLSQLParserTst {

    @Test
    public void testParsing() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoStatement.pls"));
        ASTInput input = parsePLSQL(code);
    }
}
