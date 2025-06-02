/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class CursorAttributesTest extends AbstractPLSQLParserTst {

    @Test
    void parseCursorWithAttribute() {
        ASTInput input = plsql.parseResource("CursorAttributes.pls");
        ASTExpression exp = input.descendants(ASTIfStatement.class).first().firstChild(ASTExpression.class);
        assertEquals("TestSearch%NOTFOUND", exp.getImage());
    }

    @Test
    void parseImplicitCursorAttributeBulkExceptions() {
        plsql.parseResource("CursorAttributesBulkExceptions.pls");
    }

}
