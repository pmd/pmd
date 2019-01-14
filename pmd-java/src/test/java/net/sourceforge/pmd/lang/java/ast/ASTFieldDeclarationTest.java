/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ParserTstUtil.parseJava14;
import static net.sourceforge.pmd.lang.java.ParserTstUtil.parseJava15;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import net.sourceforge.pmd.lang.java.ast.testdata.InterfaceWithNestedClass;

public class ASTFieldDeclarationTest {

    @Test
    public void testIsArray() {
        ASTCompilationUnit cu = parseJava14(TEST1);
        Dimensionable node = cu.findDescendantsOfType(ASTFieldDeclaration.class).get(0);
        assertTrue(node.isArray());
        assertEquals(1, node.getArrayDepth());
    }

    @Test
    public void testMultiDimensionalArray() {
        ASTCompilationUnit cu = parseJava14(TEST2);
        Dimensionable node = cu.findDescendantsOfType(ASTFieldDeclaration.class).get(0);
        assertEquals(3, node.getArrayDepth());
    }

    @Test
    public void testIsSyntacticallyPublic() {
        ASTCompilationUnit cu = parseJava14(TEST3);
        ASTFieldDeclaration node = cu.findDescendantsOfType(ASTFieldDeclaration.class).get(0);
        assertFalse(node.isSyntacticallyPublic());
        assertFalse(node.isPackagePrivate());
        assertFalse(node.isPrivate());
        assertFalse(node.isProtected());
        assertTrue(node.isFinal());
        assertTrue(node.isStatic());
        assertTrue(node.isPublic());
    }

    @Test
    public void testWithEnum() {
        ASTCompilationUnit cu = parseJava15(TEST4);
        ASTFieldDeclaration node = cu.findDescendantsOfType(ASTFieldDeclaration.class).get(0);
        assertFalse(node.isInterfaceMember());
    }

    @Test
    public void testWithAnnotation() {
        ASTCompilationUnit cu = parseJava15(TEST5);
        ASTFieldDeclaration node = cu.findDescendantsOfType(ASTFieldDeclaration.class).get(0);
        assertFalse(node.isInterfaceMember());
        assertTrue(node.isAnnotationMember());
    }

    private static final String TEST1 = "class Foo {" + PMD.EOL + " String[] foo;" + PMD.EOL + "}";

    private static final String TEST2 = "class Foo {" + PMD.EOL + " String[][][] foo;" + PMD.EOL + "}";

    private static final String TEST3 = "interface Foo {" + PMD.EOL + " int BAR = 6;" + PMD.EOL + "}";

    private static final String TEST4 = "public enum Foo {" + PMD.EOL + " FOO(1);" + PMD.EOL + " private int x;"
            + PMD.EOL + "}";

    private static final String TEST5 = "public @interface Foo {" + PMD.EOL + " int BAR = 6;" + PMD.EOL + "}";

    @Test
    public void testGetVariableName() {
        int id = 0;
        ASTFieldDeclaration n = new ASTFieldDeclaration(id++);
        ASTType t = new ASTType(id++);
        ASTVariableDeclarator decl = new ASTVariableDeclarator(id++);
        ASTVariableDeclaratorId declid = new ASTVariableDeclaratorId(id++);
        n.jjtAddChild(t, 0);
        t.jjtAddChild(decl, 0);
        decl.jjtAddChild(declid, 0);
        declid.setImage("foo");

        assertEquals("foo", n.getVariableName());

    }

    @Test
    public void testPrivateFieldInNestedClassInsideInterface() {
        ASTCompilationUnit cu = ParserTstUtil.parseJava10(InterfaceWithNestedClass.class);
        List<ASTFieldDeclaration> fields = cu.findDescendantsOfType(ASTFieldDeclaration.class, true);
        assertEquals(2, fields.size());
        assertEquals("MAPPING", fields.get(0).getFirstDescendantOfType(ASTVariableDeclaratorId.class).getImage());
        assertTrue(fields.get(0).isPublic());
        assertFalse(fields.get(0).isPrivate());
        assertEquals("serialVersionUID", fields.get(1).getFirstDescendantOfType(ASTVariableDeclaratorId.class).getImage());
        assertFalse(fields.get(1).isPublic());
        assertTrue(fields.get(1).isPrivate());
    }
}
