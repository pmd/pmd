package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.JavaParser;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

public class EncodingTest extends TestCase {

    public void testDecodingOfPlatformEncoding() throws Throwable {
        String platformEncoding = System.getProperty("file.encoding");
        String iso = "ISO-8859-1";
        String utf8 = "UTF-8";

        TargetJDK1_4 targetJDK1_4 = new TargetJDK1_4();
        String code = new String(TEST_UTF8.getBytes(), utf8);
        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(code.getBytes()));
        ASTCompilationUnit acu = targetJDK1_4.createParser(isr).CompilationUnit();
        String methodName = ((ASTMethodDeclarator)acu.findChildrenOfType(ASTMethodDeclarator.class).get(0)).getImage();
        assertEquals(new String("é".getBytes(), utf8), methodName);
    }

    private static final String TEST_UTF8 =
    "class Foo {" + PMD.EOL +
    " void é() {}" + PMD.EOL +
    " void fiddle() {}" + PMD.EOL +
    "}";
}
