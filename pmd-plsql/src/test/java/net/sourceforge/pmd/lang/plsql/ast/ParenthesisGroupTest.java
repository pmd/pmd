/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;
import org.junit.jupiter.api.Test;

class ParenthesisGroupTest extends AbstractPLSQLParserTst {

    @Test
    void parseParenthesisGroup0() {
        doTest("ParenthesisGroup0");
    }

    @Test
    void parseParenthesisGroup1() {
        doTest("ParenthesisGroup1");
    }

    @Test
    void parseParenthesisGroup2() {
        doTest("ParenthesisGroup2");
    }
}
