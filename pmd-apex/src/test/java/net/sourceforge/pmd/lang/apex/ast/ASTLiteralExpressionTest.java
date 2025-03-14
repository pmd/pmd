/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ASTLiteralExpressionTest extends ApexParserTestBase {

    @Test
    void doubleLiteral() {
        ASTLiteralExpression literal = createLiteral("1.2d");
        assertTrue(literal.isDouble());
        assertEquals(ASTLiteralExpression.LiteralType.DOUBLE, literal.getLiteralType());
        assertEquals("1.2", literal.getImage());

        literal = createLiteral("1.2D");
        assertTrue(literal.isDouble());
        assertEquals(ASTLiteralExpression.LiteralType.DOUBLE, literal.getLiteralType());
    }

    @Test
    void decimalLiteral() {
        ASTLiteralExpression literal = createLiteral("1.2");
        assertTrue(literal.isDecimal());
        assertEquals(ASTLiteralExpression.LiteralType.DECIMAL, literal.getLiteralType());
        assertEquals("1.2", literal.getImage());
    }

    @Test
    void stringLiteral() {
        ASTLiteralExpression literal = createLiteral("'foo'");
        assertTrue(literal.isString());
        assertEquals(ASTLiteralExpression.LiteralType.STRING, literal.getLiteralType());
        assertEquals("foo", literal.getImage());
    }

    private ASTLiteralExpression createLiteral(String code) {
        return parse("class Foo { object field = " + code + "; }")
                .descendants(ASTLiteralExpression.class)
                .first();
    }
}
