/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;
import org.junit.jupiter.api.Test;

class SelectIntoWithGroupByTest extends AbstractPLSQLParserTst {

    @Test
    void testExample1() {
        doTest("SelectIntoWithGroupBy1");
    }

    @Test
    void testExample2() {
        doTest("SelectIntoWithGroupBy2");
    }

    @Test
    void testExample3WithCube() {
        doTest("SelectIntoWithGroupBy3");
    }

    @Test
    void testExample4WithGroupingSets() {
        doTest("SelectIntoWithGroupBy4");
    }
}
