package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.JavaParser;

import java.io.StringReader;
import java.util.List;

public class EncodingTest extends TestCase {

    public void testDecodingOfUTF8() throws Throwable {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST_UTF8));
        ASTCompilationUnit acu = parser.CompilationUnit();
        List kids = acu.findChildrenOfType(ASTMethodDeclarator.class);
        assertEquals("é", ((ASTMethodDeclarator)kids.get(0)).getImage());
    }

    private static final String TEST_UTF8 =
    "class Foo {" + PMD.EOL +
    " void é() {}" + PMD.EOL +
    " void fiddle() {}" + PMD.EOL +
    "}";
}
