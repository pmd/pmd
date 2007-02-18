package test.net.sourceforge.pmd.ast;
import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;

import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
public class EncodingTest {

    @Ignore("FIXME")
    @Test
    public void testDecodingOfUTF8() throws Throwable {
        //String platformEncoding = System.getProperty("file.encoding");
        //String encoding = "ISO-8859-1";
        String encoding = "UTF-8";

        String code = new String(TEST_UTF8.getBytes(), encoding);
        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(code.getBytes()));
        ASTCompilationUnit acu = new TargetJDK1_4().createParser(isr).CompilationUnit();
        String methodName = acu.findChildrenOfType(ASTMethodDeclarator.class).get(0).getImage();
        assertEquals(new String("é".getBytes(), encoding), methodName);
    }

    private static final String TEST_UTF8 =
            "class Foo {" + PMD.EOL +
            " void é() {}" + PMD.EOL +
            " void fiddle() {}" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(EncodingTest.class);
    }
}
