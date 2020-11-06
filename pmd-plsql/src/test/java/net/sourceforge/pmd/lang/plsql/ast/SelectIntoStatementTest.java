/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class SelectIntoStatementTest extends AbstractPLSQLParserTst {

    @Test
    public void testParsingComplex() {
        plsql.parseResource("SelectIntoStatement.pls");
    }

    @Test
    public void testParsingExample1() {
        plsql.parseResource("SelectIntoStatementExample1.pls");
    }

    @Test
    public void testParsingExample2() {
        plsql.parseResource("SelectIntoStatementExample2.pls");
    }

    @Test
    public void testParsingExample3() {
        plsql.parseResource("SelectIntoStatementExample3.pls");
    }

    @Test
    public void testParsingExample4() {
        plsql.parseResource("SelectIntoStatementExample4.pls");
    }

    @Test
    public void testParsingExample5() {
        plsql.parseResource("SelectIntoStatementExample5.pls");
    }

    @Test
    public void testParsingWithFunctionCall() {
        plsql.parseResource("SelectIntoStatementFunctionCall.pls");
    }

    @Test
    public void testParsingIntoRecordField() {
        plsql.parseResource("SelectIntoStatementRecordField.pls");
    }
}
