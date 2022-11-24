/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class TrimWithRecordTypeTest extends AbstractPLSQLParserTst {

    @Test
    void parseTrimWithRecordType() {
        plsql.parseResource("TrimWithRecordType.pls");
    }
}
