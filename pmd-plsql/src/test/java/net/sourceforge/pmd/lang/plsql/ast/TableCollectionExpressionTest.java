/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class TableCollectionExpressionTest extends AbstractPLSQLParserTst {

    @Test
    public void testExamples() {
        plsql.parseResource("TableCollectionExpressionExamples.pls");
    }

    @Test
    public void testIssue1526() {
        plsql.parseResource("TableCollectionExpressionIssue1526.pls");
    }
}
