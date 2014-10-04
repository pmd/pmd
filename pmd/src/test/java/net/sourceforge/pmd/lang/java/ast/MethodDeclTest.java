/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ParserTst;

import org.junit.Test;

public class MethodDeclTest extends ParserTst {

    @Test
    public void testPublic() throws Throwable {
        String[] access = {"public"};
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be public.", amd.isPublic());
    }

    @Test
    public void testPrivate() throws Throwable {
        String[] access = {"private"};
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be private.", amd.isPrivate());
    }

    @Test
    public void testProtected() throws Throwable {
        String[] access = {"protected"};
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be protected.", amd.isProtected());
    }

    @Test
    public void testFinal() throws Throwable {
        String[] access = {"public", "final"};
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be final.", amd.isFinal());
        assertTrue("Expecting method to be public.", amd.isPublic());
    }

    @Test
    public void testSynchronized() throws Throwable {
        String[] access = {"public", "synchronized"};
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be synchronized.", amd.isSynchronized());
        assertTrue("Expecting method to be public.", amd.isPublic());
    }

    @Test
    public void testAbstract() throws Throwable {
        String[] access = {"public", "abstract"};
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be abstract.", amd.isAbstract());
        assertTrue("Expecting method to be public.", amd.isPublic());
    }

    @Test
    public void testNative() throws Throwable {
        String[] access = {"private", "native"};
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be native.", amd.isNative());
        assertTrue("Expecting method to be private.", amd.isPrivate());
    }

    @Test
    public void testStrict() throws Throwable {
        String[] access = {"public", "strictfp"};
        ASTMethodDeclaration amd = getMethodDecl(access);
        assertTrue("Expecting method to be strict.", amd.isStrictfp());
        assertTrue("Expecting method to be public.", amd.isPublic());
    }

    public ASTMethodDeclaration getMethodDecl(String[] access) throws Throwable {
        String javaCode = "public class Test { ";
        for (int i = 0; i < access.length; i++) {
            javaCode += access[i] + " ";
        }

        javaCode += " void stuff() { } }";

        Set<ASTMethodDeclaration> methods = getNodes(ASTMethodDeclaration.class, javaCode);

        assertEquals("Wrong number of methods", 1, methods.size());

        return methods.iterator().next();
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(MethodDeclTest.class);
    }
}
