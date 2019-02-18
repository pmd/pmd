/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class SelectIntoStatementTest extends AbstractPLSQLParserTst {

    @Test
    public void testParsingComplex() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoStatement.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testParsingExample1() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoStatementExample1.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testParsingExample2() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoStatementExample2.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testParsingExample3() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoStatementExample3.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testParsingExample4() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoStatementExample4.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testParsingExample5() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoStatementExample5.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testParsingWithFunctionCall() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoStatementFunctionCall.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testParsingIntoRecordField() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoStatementRecordField.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }
}
