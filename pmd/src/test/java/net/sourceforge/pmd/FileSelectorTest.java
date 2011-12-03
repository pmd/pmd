package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;

import java.io.File;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageFilenameFilter;

import org.junit.Test;

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
        LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(Language.JAVA);

        File javaFile = new File("/path/to/myFile.java");

        boolean selected = fileSelector.accept(javaFile.getParentFile(), javaFile.getName());
        assertEquals("This file should be selected !",true, selected);
    }

    
    /**
     * Test unwanted selection of a non source file.
     */
    @Test
    public void testUnwantedFile() {
        LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(Language.JAVA);

        File javaFile = new File("/path/to/myFile.txt");

        boolean selected = fileSelector.accept(javaFile.getParentFile(), javaFile.getName());
        assertEquals("Not-source file must not be selected!", false, selected);
    }

    /**
     * Test unwanted selection of a java file.
     */
    @Test
    public void testUnwantedJavaFile() {
        LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(Language.XML);

        File javaFile = new File("/path/to/MyClass.java");

        boolean selected = fileSelector.accept(javaFile.getParentFile(), javaFile.getName());
        assertEquals("Unwanted java file must not be selected!", false, selected);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(FileSelectorTest.class);
    }
}
