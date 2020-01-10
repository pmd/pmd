/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class InsertIntoClauseTest extends AbstractPLSQLParserTst {

    @Test
    public void parseInsertInto() {
        plsql.parseResource("InsertIntoClause.pls");
    }

    @Test
    public void parseInsertIntoReturning() {
        plsql.parseResource("InsertIntoClauseReturning.pls");
    }

    @Test
    public void parseInsertIntoWithRecord() {
        plsql.parseResource("InsertIntoClauseRecord.pls");
    }
}
