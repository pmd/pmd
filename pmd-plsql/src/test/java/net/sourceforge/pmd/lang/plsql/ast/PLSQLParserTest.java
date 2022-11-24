/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class PLSQLParserTest extends AbstractPLSQLParserTst {

    @Test
    void testExceptions() {
        plsql.parse("CREATE OR REPLACE PROCEDURE bar IS BEGIN" + "    doSomething;" + "    EXCEPTION"
                       + "    WHEN FooException THEN" + "        doSomethingElse;" + "    WHEN OTHERS THEN"
                       + "        doSomethingElse;" + "END;");
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1167/
     */
    @Test
    void testBOM() {
        plsql.parse("\ufeff" + "CREATE OR REPLACE PROCEDURE bar IS BEGIN" + "    doSomething;" + "    EXCEPTION"
                       + "    WHEN FooException THEN" + "        doSomethingElse;" + "    WHEN OTHERS THEN"
                       + "        doSomethingElse;" + "END;");
    }

    @Test
    void testBug1531() {
        assertTimeout(Duration.of(5, ChronoUnit.SECONDS), () ->
            plsql.parse("create or replace force view oxa.o_xa_function_role_types as\n"
                           + "select \"CFT_ID\",\"CFR_ID\",\"CFT_NAME\",\"TCN\",\"LOG_MODULE\",\"LOG_USER\",\"LOG_DATE\",\"LOG_TIME\" from crm_function_role_types\n"
                           + "/"));
    }

    @Test
    void testBug1527() {
        plsql.parseResource("InlinePragmaProcError.pls");
    }

    @Test
    void testBug1520IsOfType() {
        plsql.parseResource("IsOfType.pls");
    }

    @Test
    void testBug1520Using() {
        plsql.parseResource("Using.pls");
    }

    @Test
    void testSingleLineSelect() {
        plsql.parseResource("SingleLineSelect.pls");
    }

    @Test
    void testMultiLineSelect() {
        plsql.parseResource("MultiLineSelect.pls");
    }

    @Test
    void testIsNull() {
        plsql.parseResource("IsNull.pls");
    }

    @Test
    void testCodingStyleExample() {
        plsql.parseResource("CodingStyleExample.pls");
    }

    @Test
    void testCaseIssue1454() {
        plsql.parseResource("CaseIssue1454.pls");
    }

    @Test
    void testRelationalOperators() {
        // https://github.com/pmd/pmd/issues/3746
        plsql.parseResource("RelationalOperators.pls");
    }

    @Test
    void testExecuteImmediateIssue3106() {
        plsql.parseResource("ExecuteImmediateIssue3106.pls");
    }
}
