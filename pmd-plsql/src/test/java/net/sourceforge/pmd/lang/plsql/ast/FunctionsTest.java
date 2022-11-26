/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class FunctionsTest extends AbstractPLSQLParserTst {

    @Test
    void parseTrimCall() {
        plsql.parseResource("TrimFunction.pls");
    }

    @Test
    void parseSelectExtractExpression() {
        plsql.parseResource("ExtractExpressions.pls");
    }

    @Test
    void parseXMLExpression() {
        plsql.parseResource("XMLFunctions.pls");
    }
}
