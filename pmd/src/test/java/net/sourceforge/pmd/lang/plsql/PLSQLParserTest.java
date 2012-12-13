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
}
