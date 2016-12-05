/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class PLSQLParserTest extends AbstractPLSQLParserTst {

    @Test
    public void testExceptions() {
        parsePLSQL("CREATE OR REPLACE PROCEDURE bar IS BEGIN" + "    doSomething;" + "    EXCEPTION"
                + "    WHEN FooException THEN" + "        doSomethingElse;" + "    WHEN OTHERS THEN"
                + "        doSomethingElse;" + "END;");
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1167/
     */
    @Test
    public void testBOM() {
        parsePLSQL("\ufeff" + "CREATE OR REPLACE PROCEDURE bar IS BEGIN" + "    doSomething;" + "    EXCEPTION"
                + "    WHEN FooException THEN" + "        doSomethingElse;" + "    WHEN OTHERS THEN"
                + "        doSomethingElse;" + "END;");
    }

    @Test(timeout = 5000)
    public void testBug1531() {
        parsePLSQL("create or replace force view oxa.o_xa_function_role_types as\n"
                + "select \"CFT_ID\",\"CFR_ID\",\"CFT_NAME\",\"TCN\",\"LOG_MODULE\",\"LOG_USER\",\"LOG_DATE\",\"LOG_TIME\" from crm_function_role_types\n"
                + "/");
    }

    @Test
    public void testBug1527() throws Exception {
        parsePLSQL(IOUtils.toString(PLSQLParserTest.class.getResourceAsStream("ast/InlinePragmaProcError.pls")));
    }

    @Test
    public void testBug1520IsOfType() throws Exception {
        parsePLSQL(IOUtils.toString(PLSQLParserTest.class.getResourceAsStream("ast/IsOfType.pls")));
    }

    @Test
    public void testBug1520Using() throws Exception {
        parsePLSQL(IOUtils.toString(PLSQLParserTest.class.getResourceAsStream("ast/Using.pls")));
    }

    @Test
    public void testSingleLineSelect() throws Exception {
        parsePLSQL(IOUtils.toString(PLSQLParserTest.class.getResourceAsStream("ast/SingleLineSelect.pls")));
    }

    @Test
    public void testMultiLineSelect() throws Exception {
        parsePLSQL(IOUtils.toString(PLSQLParserTest.class.getResourceAsStream("ast/MultiLineSelect.pls")));
    }

    @Test
    public void testIsNull() throws Exception {
        parsePLSQL(IOUtils.toString(PLSQLParserTest.class.getResourceAsStream("ast/IsNull.pls")));
    }
}
