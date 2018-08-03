/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class SelectIntoStatementTest extends AbstractPLSQLParserTst {

    @Test
    public void testParsingComplex() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoStatement.pls"));
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testParsingExample1() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoStatementExample1.pls"));
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testParsingExample2() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoStatementExample2.pls"));
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testParsingExample3() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoStatementExample3.pls"));
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testParsingExample4() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoStatementExample4.pls"));
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testParsingExample5() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoStatementExample5.pls"));
        ASTInput input = parsePLSQL(code);
    }
}
