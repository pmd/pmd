/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.JavaParser;

import java.io.StringReader;

public class ASTTypeTest extends TestCase{

    public void testIsArray() {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST1));
        ASTCompilationUnit cu = parser.CompilationUnit();
        ASTType node = (ASTType)cu.findChildrenOfType(ASTType.class).get(0);
        assertTrue(node.isArray());
    }

    public void testOneDimensionArray() {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST2));
        ASTCompilationUnit cu = parser.CompilationUnit();
        ASTType node = (ASTType)cu.findChildrenOfType(ASTType.class).get(0);
        assertEquals(1, node.getDimensions());
    }

    public void testMultiDimensionalArray() {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST3));
        ASTCompilationUnit cu = parser.CompilationUnit();
        ASTType node = (ASTType)cu.findChildrenOfType(ASTType.class).get(0);
        assertEquals(3, node.getDimensions());
    }

    private static final String TEST1 =
    "class Foo {" + PMD.EOL +
    " String[] foo() {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "class Foo {" + PMD.EOL +
    " String[] foo() {}" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "class Foo {" + PMD.EOL +
    " String[][][] foo() {}" + PMD.EOL +
    "}";

}
