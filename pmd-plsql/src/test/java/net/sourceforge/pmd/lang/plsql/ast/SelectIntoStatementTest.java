/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class SelectIntoStatementTest extends AbstractPLSQLParserTst {

    @Test
    void testParsingComplex() {
        plsql.parseResource("SelectIntoStatement.pls");
    }

    @Test
    void testParsingExample1() {
        plsql.parseResource("SelectIntoStatementExample1.pls");
    }

    @Test
    void testParsingExample2() {
        plsql.parseResource("SelectIntoStatementExample2.pls");
    }

    @Test
    void testParsingExample3() {
        plsql.parseResource("SelectIntoStatementExample3.pls");
    }

    @Test
    void testParsingExample4() {
        plsql.parseResource("SelectIntoStatementExample4.pls");
    }

    @Test
    void testParsingExample5() {
        plsql.parseResource("SelectIntoStatementExample5.pls");
    }

    @Test
    void testParsingExample6Invalid() {
        assertThrows(ParseException.class, () -> plsql.parseResource("SelectIntoStatementExample6Invalid.pls"));
    }

    @Test
    void testParsingWithFunctionCall() {
        plsql.parseResource("SelectIntoStatementFunctionCall.pls");
    }

    @Test
    void testParsingIntoRecordField() {
        plsql.parseResource("SelectIntoStatementRecordField.pls");
    }
}
