package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Dimensionable;
import net.sourceforge.pmd.ast.JavaParser;
import test.net.sourceforge.pmd.testframework.ParserTst;

import java.io.StringReader;

public class ASTFieldDeclarationTest extends ParserTst {

    public void testIsArray() {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST1));
        ASTCompilationUnit cu = parser.CompilationUnit();
        Dimensionable node = (Dimensionable) cu.findChildrenOfType(ASTFieldDeclaration.class).get(0);
        assertTrue(node.isArray());
        assertEquals(1, node.getArrayDepth());
    }

    public void testMultiDimensionalArray() {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST2));
        ASTCompilationUnit cu = parser.CompilationUnit();
        Dimensionable node = (Dimensionable) cu.findChildrenOfType(ASTFieldDeclaration.class).get(0);
        assertEquals(3, node.getArrayDepth());
    }

    public void testIsSyntacticallyPublic() {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST3));
        ASTCompilationUnit cu = parser.CompilationUnit();
        ASTFieldDeclaration node = (ASTFieldDeclaration) cu.findChildrenOfType(ASTFieldDeclaration.class).get(0);
        assertFalse(node.isSyntacticallyPublic());
        assertFalse(node.isPackagePrivate());
        assertFalse(node.isPrivate());
        assertFalse(node.isProtected());
        assertTrue(node.isFinal());
        assertTrue(node.isStatic());
        assertTrue(node.isPublic());
    }

    public void testWithEnum() {
        JavaParser parser = (new TargetJDK1_5()).createParser(new StringReader(TEST4));
        ASTCompilationUnit cu = parser.CompilationUnit();
        ASTFieldDeclaration node = (ASTFieldDeclaration) cu.findChildrenOfType(ASTFieldDeclaration.class).get(0);
        assertFalse(node.isInterfaceMember());
    }

    private static final String TEST1 =
            "class Foo {" + PMD.EOL +
            " String[] foo;" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "class Foo {" + PMD.EOL +
            " String[][][] foo;" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "interface Foo {" + PMD.EOL +
            " int BAR = 6;" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public enum Foo {" + PMD.EOL +
            " FOO(1);" + PMD.EOL +
            " private int x;" + PMD.EOL +
            "}";

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
}
