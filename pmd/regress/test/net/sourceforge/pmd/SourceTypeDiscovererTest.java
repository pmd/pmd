package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.SourceTypeDiscoverer;

import java.io.File;

public class SourceTypeDiscovererTest extends TestCase {

    /**
     * Test on JSP file.
     */
    public void testJspFile() {
        SourceTypeDiscoverer discoverer = new SourceTypeDiscoverer();
        File jspFile = new File("/path/to/MyPage.jsp");

        SourceType type = discoverer.getSourceTypeOfFile(jspFile);

        assertEquals("SourceType must be JSP!", SourceType.JSP, type);
    }

    /**
     * Test on Java file with default options.
     */
    public void testJavaFileUsingDefaults() {
        SourceTypeDiscoverer discoverer = new SourceTypeDiscoverer();
        File javaFile = new File("/path/to/MyClass.java");

        SourceType type = discoverer.getSourceTypeOfFile(javaFile);

        assertEquals("SourceType must be Java 1.4!", SourceType.JAVA_14, type);
    }

    /**
     * Test on Java file with Java version set to 1.5.
     */
    public void testJavaFileUsing15() {
        SourceTypeDiscoverer discoverer = new SourceTypeDiscoverer();
        discoverer.setSourceTypeOfJavaFiles(SourceType.JAVA_15);
        File javaFile = new File("/path/to/MyClass.java");

        SourceType type = discoverer.getSourceTypeOfFile(javaFile);

        assertEquals("SourceType must be Java 1.5!", SourceType.JAVA_15, type);
    }
}
