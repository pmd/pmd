/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class ASTBooleanLiteralTest extends BaseParserTest {

    @Test
    void testTrue() {
        List<ASTBooleanLiteral> ops = java.getNodes(ASTBooleanLiteral.class, TEST1);
        ASTBooleanLiteral b = ops.get(0);
        assertTrue(b.isTrue());
    }

    @Test
    void testFalse() {
        List<ASTBooleanLiteral> ops = java.getNodes(ASTBooleanLiteral.class, TEST2);
        ASTBooleanLiteral b = ops.get(0);
        assertFalse(b.isTrue());
    }

    private static final String TEST1 = "class Foo { \n boolean bar = true; \n} ";

    private static final String TEST2 = "class Foo { \n boolean bar = false; \n} ";
}
