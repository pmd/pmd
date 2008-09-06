package test.net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.SourceFileSelector;
import net.sourceforge.pmd.lang.Language;

import org.junit.Test;

import java.io.File;
/**
 * Tests on FileSelector.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class FileSelectorTest {

    /**
     * Test wanted selection of a source file.
     */
    @Test
    public void testWantedFile() {
        SourceFileSelector fileSelector = new SourceFileSelector(Language.JAVA);

        File javaFile = new File("/path/to/myFile.java");

        boolean selected = fileSelector.isWantedFile(javaFile);
        assertEquals("This file should be selected !",true, selected);
    }

    
    /**
     * Test unwanted selection of a non source file.
     */
    @Test
    public void testUnwantedFile() {
        SourceFileSelector fileSelector = new SourceFileSelector(Language.JAVA);

        File javaFile = new File("/path/to/myFile.txt");

        boolean selected = fileSelector.isWantedFile(javaFile);
        assertEquals("Not-source file must not be selected!", false, selected);
    }

    /**
     * Test unwanted selection of a java file.
     */
    @Test
    public void testUnwantedJavaFile() {
        SourceFileSelector fileSelector = new SourceFileSelector(Language.XML);

        File javaFile = new File("/path/to/MyClass.java");

        boolean selected = fileSelector.isWantedFile(javaFile);
        assertEquals("Unwanted java file must not be selected!", false, selected);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(FileSelectorTest.class);
    }
}
