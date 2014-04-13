/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql;

import org.junit.Test;


public class PLSQLParserTest extends AbstractPLSQLParserTst {

	@Test
	public void testExceptions() {
		parsePLSQL(
			"CREATE OR REPLACE PROCEDURE bar IS BEGIN"
		  + "    doSomething;"
		  + "    EXCEPTION"
		  + "    WHEN FooException THEN"
		  + "        doSomethingElse;"
		  + "    WHEN OTHERS THEN"
		  + "        doSomethingElse;"
		  + "END;");
	}

    /**
     * See https://sourceforge.net/p/pmd/bugs/1167/
     */
    @Test
    public void testBOM() {
        parsePLSQL("\ufeff" +
            "CREATE OR REPLACE PROCEDURE bar IS BEGIN"
          + "    doSomething;"
          + "    EXCEPTION"
          + "    WHEN FooException THEN"
          + "        doSomethingElse;"
          + "    WHEN OTHERS THEN"
          + "        doSomethingElse;"
          + "END;");
    }
}
