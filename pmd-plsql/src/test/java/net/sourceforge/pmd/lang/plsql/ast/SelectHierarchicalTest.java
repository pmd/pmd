/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;
import org.junit.jupiter.api.Test;

class SelectHierarchicalTest extends AbstractPLSQLParserTst {

    @Test
    void parseSelectHierarchicalQueries() {
        doTest("SelectHierarchical");
    }
}
