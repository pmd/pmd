/*
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
        doTest("SelectIntoStatement");
    }

    @Test
    void testParsingExample1() {
        doTest("SelectIntoStatementExample1");
    }

    @Test
    void testParsingExample2() {
        doTest("SelectIntoStatementExample2");
    }

    @Test
    void testParsingExample3() {
        doTest("SelectIntoStatementExample3");
    }

    @Test
    void testParsingExample4() {
        doTest("SelectIntoStatementExample4");
    }

    @Test
    void testParsingExample5() {
        doTest("SelectIntoStatementExample5");
    }

    @Test
    void testParsingExample6Invalid() {
        assertThrows(ParseException.class, () -> doTest("SelectIntoStatementExample6Invalid"));
    }

    @Test
    void testParsingWithFunctionCall() {
        doTest("SelectIntoStatementFunctionCall");
    }

    @Test
    void testParsingIntoRecordField() {
        doTest("SelectIntoStatementRecordField");
    }

    @Test
    void selectIntoTimeoutIssue5521() {
        doTest("SelectIntoTimeoutIssue5521");
    }
}
