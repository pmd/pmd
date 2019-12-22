/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ASTLocalVariableDeclarationTest extends BaseParserTest {

    @Test
    public void testSingleDimArray() {
        ASTLocalVariableDeclaration node = java.parse(TEST1).findDescendantsOfType(ASTLocalVariableDeclaration.class).get(0);
        assertEquals(1, node.getArrayDepth());
    }

    @Test
    public void testMultDimArray() {
        ASTLocalVariableDeclaration node = java.parse(TEST2).findDescendantsOfType(ASTLocalVariableDeclaration.class).get(0);
        assertEquals(2, node.getArrayDepth());
    }

    @Test
    public void testMultDimArraySplitBraces() {
        ASTLocalVariableDeclaration node = java.parse(TEST3).findDescendantsOfType(ASTLocalVariableDeclaration.class).get(0);
        assertEquals(3, node.getArrayDepth());
    }

    private static final String TEST1 = "class Foo {\n void bar() {int x[] = null;}\n}";

    private static final String TEST2 = "class Foo {\n void bar() {int x[][] = null;}\n}";

    private static final String TEST3 = "class Foo {\n void bar() {int[] x[][] = null;}\n}";
}
