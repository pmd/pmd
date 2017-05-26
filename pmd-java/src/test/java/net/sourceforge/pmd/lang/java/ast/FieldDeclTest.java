/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.ParserTst;

public class FieldDeclTest extends ParserTst {

    public String makeAccessJavaCode(String[] access) {
        String result = "public class Test { ";
        for (int i = 0; i < access.length; i++) {
            result += access[i] + " ";
        }
        return result + " int j;  }";
    }

    public ASTFieldDeclaration getFieldDecl(String[] access) {
        Set<ASTFieldDeclaration> fields = getNodes(ASTFieldDeclaration.class, makeAccessJavaCode(access));

        assertEquals("Wrong number of fields", 1, fields.size());
        return fields.iterator().next();
    }

    @Test
    public void testPublic() {
        String[] access = { "public" };
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be public.", afd.isPublic());
    }

    @Test
    public void testProtected() {
        String[] access = { "protected" };
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be protected.", afd.isProtected());
    }

    @Test
    public void testPrivate() {
        String[] access = { "private" };
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be private.", afd.isPrivate());
    }

    @Test
    public void testStatic() {
        String[] access = { "private", "static" };
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be static.", afd.isStatic());
        assertTrue("Expecting field to be private.", afd.isPrivate());
    }

    @Test
    public void testFinal() {
        String[] access = { "public", "final" };
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be final.", afd.isFinal());
        assertTrue("Expecting field to be public.", afd.isPublic());
    }

    @Test
    public void testTransient() {
        String[] access = { "private", "transient" };
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be private.", afd.isPrivate());
        assertTrue("Expecting field to be transient.", afd.isTransient());
    }

    @Test
    public void testVolatile() {
        String[] access = { "private", "volatile" };
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be volatile.", afd.isVolatile());
        assertTrue("Expecting field to be private.", afd.isPrivate());
    }
}
