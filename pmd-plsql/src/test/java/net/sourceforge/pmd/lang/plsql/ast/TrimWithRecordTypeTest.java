/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class TrimWithRecordTypeTest extends AbstractPLSQLParserTst {

    @Test
    public void parseTrimWithRecordType() {
        plsql.parseResource("TrimWithRecordType.pls");
    }
}
