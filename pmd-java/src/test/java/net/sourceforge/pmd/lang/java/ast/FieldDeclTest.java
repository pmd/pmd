/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ast.AccessNodeTest.getDeclWithModifiers;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FieldDeclTest extends BaseParserTest {


    public ASTFieldDeclaration getFieldDecl(String[] access) {
        return getDeclWithModifiers(access, ASTFieldDeclaration.class, "int j;");
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
