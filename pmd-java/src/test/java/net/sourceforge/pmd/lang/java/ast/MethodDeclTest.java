/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MethodDeclTest {

    @Test
    public void testPublic() {
        String[] access = { "public" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be public.", amd.isPublic());
    }

    @Test
    public void testPrivate() {
        String[] access = { "private" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be private.", amd.isPrivate());
    }

    @Test
    public void testProtected() {
        String[] access = { "protected" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be protected.", amd.isProtected());
    }

    @Test
    public void testFinal() {
        String[] access = { "public", "final" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be final.", amd.isFinal());
        assertTrue("Expecting method to be public.", amd.isPublic());
    }

    @Test
    public void testSynchronized() {
        String[] access = { "public", "synchronized" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be synchronized.", amd.isSynchronized());
        assertTrue("Expecting method to be public.", amd.isPublic());
    }

    @Test
    public void testAbstract() {
        String[] access = { "public", "abstract" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be abstract.", amd.isAbstract());
        assertTrue("Expecting method to be public.", amd.isPublic());
    }

    @Test
    public void testNative() {
        String[] access = { "private", "native" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be native.", amd.isNative());
        assertTrue("Expecting method to be private.", amd.isPrivate());
    }

    @Test
    public void testStrict() {
        String[] access = { "public", "strictfp" };
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be strict.", amd.isStrictfp());
        assertTrue("Expecting method to be public.", amd.isPublic());
    }

    private ASTMethodDeclaration getMethodDecl(String[] access) {
        return AccessNodeTest.getDeclWithModifiers(access, ASTMethodDeclaration.class, "void stuff(){}");
    }
}
