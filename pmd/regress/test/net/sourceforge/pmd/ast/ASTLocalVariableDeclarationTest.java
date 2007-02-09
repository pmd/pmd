package test.net.sourceforge.pmd.ast;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.JavaParser;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.ParserTst;

import java.io.StringReader;

public class ASTLocalVariableDeclarationTest extends ParserTst {

    @Test
    public void testSingleDimArray() {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST1));
        ASTCompilationUnit cu = parser.CompilationUnit();
        ASTLocalVariableDeclaration node = cu.findChildrenOfType(ASTLocalVariableDeclaration.class).get(0);
        assertEquals(1, node.getArrayDepth());
    }

    @Test
    public void testMultDimArray() {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST2));
        ASTCompilationUnit cu = parser.CompilationUnit();
        ASTLocalVariableDeclaration node = cu.findChildrenOfType(ASTLocalVariableDeclaration.class).get(0);
        assertEquals(2, node.getArrayDepth());
    }

    @Test
    public void testMultDimArraySplitBraces() {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST3));
        ASTCompilationUnit cu = parser.CompilationUnit();
        ASTLocalVariableDeclaration node = cu.findChildrenOfType(ASTLocalVariableDeclaration.class).get(0);
        assertEquals(3, node.getArrayDepth());
    }

    private static final String TEST1 =
            "class Foo {" + PMD.EOL +
            " void bar() {int x[] = null;}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "class Foo {" + PMD.EOL +
            " void bar() {int x[][] = null;}" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "class Foo {" + PMD.EOL +
            " void bar() {int[] x[][] = null;}" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTLocalVariableDeclarationTest.class);
    }
}
