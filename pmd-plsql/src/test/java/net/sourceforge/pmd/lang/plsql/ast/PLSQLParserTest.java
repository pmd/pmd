/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class PLSQLParserTest extends AbstractPLSQLParserTst {

    @Test
    public void testExceptions() {
        plsql.parse("CREATE OR REPLACE PROCEDURE bar IS BEGIN" + "    doSomething;" + "    EXCEPTION"
                       + "    WHEN FooException THEN" + "        doSomethingElse;" + "    WHEN OTHERS THEN"
                       + "        doSomethingElse;" + "END;");
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1167/
     */
    @Test
    public void testBOM() {
        plsql.parse("\ufeff" + "CREATE OR REPLACE PROCEDURE bar IS BEGIN" + "    doSomething;" + "    EXCEPTION"
                       + "    WHEN FooException THEN" + "        doSomethingElse;" + "    WHEN OTHERS THEN"
                       + "        doSomethingElse;" + "END;");
    }

    @Test(timeout = 5000)
    public void testBug1531() {
        plsql.parse("create or replace force view oxa.o_xa_function_role_types as\n"
                       + "select \"CFT_ID\",\"CFR_ID\",\"CFT_NAME\",\"TCN\",\"LOG_MODULE\",\"LOG_USER\",\"LOG_DATE\",\"LOG_TIME\" from crm_function_role_types\n"
                       + "/");
    }

    @Test
    public void testBug1527() {
        plsql.parseResource("InlinePragmaProcError.pls");
    }

    @Test
    public void testBug1520IsOfType() {
        plsql.parseResource("IsOfType.pls");
    }

    @Test
    public void testBug1520Using() {
        plsql.parseResource("Using.pls");
    }

    @Test
    public void testSingleLineSelect() {
        plsql.parseResource("SingleLineSelect.pls");
    }

    @Test
    public void testMultiLineSelect() {
        plsql.parseResource("MultiLineSelect.pls");
    }

    @Test
    public void testIsNull() {
        plsql.parseResource("IsNull.pls");
    }

    @Test
    public void testCodingStyleExample() {
        plsql.parseResource("CodingStyleExample.pls");
    }

    @Test
    public void testCaseIssue1454() {
        plsql.parseResource("CaseIssue1454.pls");
    }
}
