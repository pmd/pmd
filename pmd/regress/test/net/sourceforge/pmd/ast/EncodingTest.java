package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.JavaParser;

import java.io.StringReader;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.util.List;

public class EncodingTest extends TestCase {

    public void testDecodingOfUTF8() throws Throwable {
        TargetJDK1_4 targetJDK1_4 = new TargetJDK1_4();
        ByteArrayInputStream bis = new ByteArrayInputStream(TEST_UTF8.getBytes());
        InputStreamReader isr = new InputStreamReader(bis, System.getProperty("file.encoding"));
        JavaParser parser = targetJDK1_4.createParser(isr);
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
