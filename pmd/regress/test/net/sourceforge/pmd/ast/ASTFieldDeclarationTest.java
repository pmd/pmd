package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.Dimensionable;
import net.sourceforge.pmd.ast.JavaParser;

import java.io.StringReader;

public class ASTFieldDeclarationTest extends ParserTst{

    public void testIsArray() {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST1));
        ASTCompilationUnit cu = parser.CompilationUnit();
        Dimensionable node = (Dimensionable)cu.findChildrenOfType(ASTFieldDeclaration.class).get(0);
        assertTrue(node.isArray());
        assertEquals(1, node.getArrayDepth());
    }

    public void testMultiDimensionalArray() {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST2));
        ASTCompilationUnit cu = parser.CompilationUnit();
        Dimensionable node = (Dimensionable)cu.findChildrenOfType(ASTFieldDeclaration.class).get(0);
        assertEquals(3, node.getArrayDepth());
    }


    private static final String TEST1 =
    "class Foo {" + PMD.EOL +
    " String[] foo;" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "class Foo {" + PMD.EOL +
    " String[][][] foo;" + PMD.EOL +
    "}";
}
