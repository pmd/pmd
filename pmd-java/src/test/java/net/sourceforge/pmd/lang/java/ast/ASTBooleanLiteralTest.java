/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class ASTBooleanLiteralTest extends BaseParserTest {

    @Test
    public void testTrue() {
        List<ASTBooleanLiteral> ops = java.getNodes(ASTBooleanLiteral.class, TEST1);
        ASTBooleanLiteral b = ops.get(0);
        assertTrue(b.isTrue());
    }

    @Test
    public void testFalse() {
        List<ASTBooleanLiteral> ops = java.getNodes(ASTBooleanLiteral.class, TEST2);
        ASTBooleanLiteral b = ops.get(0);
        assertFalse(b.isTrue());
    }

    private static final String TEST1 = "class Foo { \n boolean bar = true; \n} ";

    private static final String TEST2 = "class Foo { \n boolean bar = false; \n} ";
}
