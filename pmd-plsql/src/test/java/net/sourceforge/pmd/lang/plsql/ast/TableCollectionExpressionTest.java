/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;
import org.junit.jupiter.api.Test;

class TableCollectionExpressionTest extends AbstractPLSQLParserTst {

    @Test
    void testExamples() {
        plsql.parseResource("TableCollectionExpressionExamples.pls");
    }

    @Test
    void testIssue1526() {
        plsql.parseResource("TableCollectionExpressionIssue1526.pls");
    }
}
