package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

public class EncodingTest extends TestCase {

    public void testDecodingOfUTF8() throws Throwable {
        //String platformEncoding = System.getProperty("file.encoding");
        //String encoding = "ISO-8859-1";
        String encoding = "UTF-8";

        String code = new String(TEST_UTF8.getBytes(), encoding);
        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(code.getBytes()));
        //FIXME
/*
        ASTCompilationUnit acu = new TargetJDK1_4().createParser(isr).CompilationUnit();
        String methodName = ((ASTMethodDeclarator)acu.findChildrenOfType(ASTMethodDeclarator.class).get(0)).getImage();
        assertEquals(new String("é".getBytes(), encoding), methodName);
*/
    }

    private static final String TEST_UTF8 =
            "class Foo {" + PMD.EOL +
            " void é() {}" + PMD.EOL +
            " void fiddle() {}" + PMD.EOL +
            "}";
}
