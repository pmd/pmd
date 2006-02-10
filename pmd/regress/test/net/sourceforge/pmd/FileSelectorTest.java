package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.SourceFileSelector;

import java.io.File;

/**
 * Tests on FileSelector.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class FileSelectorTest extends TestCase {

    /**
     * Test default selection of .java files.
     */
    public void testSelectJavaFile() {
        SourceFileSelector fileSelector = new SourceFileSelector();

        File javaFile = new File("/path/to/MyClass.java");

        boolean selected = fileSelector.isWantedFile(javaFile);
        assertEquals("Java file must be selected!", true, selected);
    }

    /**
     * Test wanted selection of .jsp files.
     */
    public void testSelectJspFile() {
        SourceFileSelector fileSelector = new SourceFileSelector();
        fileSelector.setSelectJspFiles(true);

        File javaFile = new File("/path/to/MyPage.jsp");

        boolean selected = fileSelector.isWantedFile(javaFile);
        assertEquals("Jsp file must be selected!", true, selected);
    }

    /**
     * Test unwanted selection of a non source file.
     */
    public void testUnwantedFile() {
        SourceFileSelector fileSelector = new SourceFileSelector();

        File javaFile = new File("/path/to/myFile.txt");

        boolean selected = fileSelector.isWantedFile(javaFile);
        assertEquals("Not-source file must not be selected!", false, selected);
    }

    /**
     * Test unwanted selection of a java file.
     */
    public void testUnwantedJavaFile() {
        SourceFileSelector fileSelector = new SourceFileSelector();
        fileSelector.setSelectJavaFiles(false);

        File javaFile = new File("/path/to/MyClass.java");

        boolean selected = fileSelector.isWantedFile(javaFile);
        assertEquals("Unwanted java file must not be selected!", false, selected);
    }
}
