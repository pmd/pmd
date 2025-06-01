/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;
import org.junit.jupiter.api.Test;

class FunctionsTest extends AbstractPLSQLParserTst {

    @Test
    void parseSelectExtractExpression() {
        doTest("ExtractExpressions");
    }
}
