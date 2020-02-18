/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.testdata.InterfaceWithNestedClass;

public class ASTFieldDeclarationTest extends BaseParserTest {

    @Test
    public void testIsArray() {
        ASTCompilationUnit cu = java.parse(TEST1);
        Dimensionable node = cu.findDescendantsOfType(ASTFieldDeclaration.class).get(0);
        assertTrue(node.isArray());
        assertEquals(1, node.getArrayDepth());
    }

    @Test
    public void testMultiDimensionalArray() {
        ASTCompilationUnit cu = java.parse(TEST2);
        Dimensionable node = cu.findDescendantsOfType(ASTFieldDeclaration.class).get(0);
        assertEquals(3, node.getArrayDepth());
    }

    @Test
    public void testIsSyntacticallyPublic() {
        ASTCompilationUnit cu = java.parse(TEST3);
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
        ASTCompilationUnit cu = java5.parse(TEST4);
        ASTFieldDeclaration node = cu.findDescendantsOfType(ASTFieldDeclaration.class).get(0);
        assertFalse(node.isInterfaceMember());
    }

    @Test
    public void testWithAnnotation() {
        ASTCompilationUnit cu = java5.parse(TEST5);
        ASTFieldDeclaration node = cu.findDescendantsOfType(ASTFieldDeclaration.class).get(0);
        assertFalse(node.isInterfaceMember());
        assertTrue(node.isAnnotationMember());
    }

    private static final String TEST1 = "class Foo {\n String[] foo;\n}";

    private static final String TEST2 = "class Foo {\n String[][][] foo;\n}";

    private static final String TEST3 = "interface Foo {\n int BAR = 6;\n}";

    private static final String TEST4 = "public enum Foo {\n FOO(1);\n private int x;\n}";

    private static final String TEST5 = "public @interface Foo {\n int BAR = 6;\n}";

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
        ASTCompilationUnit cu = JavaParsingHelper.WITH_PROCESSING.parseClass(InterfaceWithNestedClass.class, "10");
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
