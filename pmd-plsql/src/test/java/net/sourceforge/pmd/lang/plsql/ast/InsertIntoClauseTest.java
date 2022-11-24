/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class InsertIntoClauseTest extends AbstractPLSQLParserTst {

    @Test
    void parseInsertInto() {
        plsql.parseResource("InsertIntoClause.pls");
    }

    @Test
    void parseInsertIntoReturning() {
        plsql.parseResource("InsertIntoClauseReturning.pls");
    }

    @Test
    void parseInsertIntoWithRecord() {
        plsql.parseResource("InsertIntoClauseRecord.pls");
    }
}
