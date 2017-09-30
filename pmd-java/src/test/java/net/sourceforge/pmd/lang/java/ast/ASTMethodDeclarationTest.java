/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.ParserTstUtil;

public class ASTMethodDeclarationTest {

    @Test
    public void testGetVariableName() {
        int id = 0;

        ASTMethodDeclaration md = new ASTMethodDeclaration(id++);
        ASTMethodDeclarator de = new ASTMethodDeclarator(id++);
        de.setImage("foo");
        md.jjtAddChild(de, 0);

        assertEquals("foo", md.getMethodName());
    }

    @Test
    public void testPrivateInterfaceMethods() {
        ASTCompilationUnit node = ParserTstUtil.parseJava9("public interface Foo { private void bar() { } }");
        ASTMethodDeclaration methodDecl = node.getFirstDescendantOfType(ASTMethodDeclaration.class);
        assertTrue(methodDecl.isPrivate());
        assertFalse(methodDecl.isPublic());
    }

    @Test
    public void testPublicInterfaceMethods() {
        ASTCompilationUnit node = ParserTstUtil.parseJava9("public interface Foo { void bar(); }");
        ASTMethodDeclaration methodDecl = node.getFirstDescendantOfType(ASTMethodDeclaration.class);
        assertFalse(methodDecl.isPrivate());
        assertTrue(methodDecl.isPublic());
    }
}
