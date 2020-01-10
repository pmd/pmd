/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class XMLTableTest extends AbstractPLSQLParserTst {

    @Test
    public void testParseXMLTable() {
        plsql.parseResource("XMLTable.pls");
    }
}
