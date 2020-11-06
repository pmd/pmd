/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class FunctionsTest extends AbstractPLSQLParserTst {

    @Test
    public void parseTrimCall() {
        plsql.parseResource("TrimFunction.pls");
    }

    @Test
    public void parseSelectExtractExpression() {
        plsql.parseResource("ExtractExpressions.pls");
    }

    @Test
    public void parseXMLExpression() {
        plsql.parseResource("XMLFunctions.pls");
    }
}
