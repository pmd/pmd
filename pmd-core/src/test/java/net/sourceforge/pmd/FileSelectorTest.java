/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageFilenameFilter;
import net.sourceforge.pmd.lang.LanguageRegistry;

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
        LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(
                LanguageRegistry.getLanguage(DummyLanguageModule.NAME));

        File javaFile = new File("/path/to/myFile.dummy");

        boolean selected = fileSelector.accept(javaFile.getParentFile(), javaFile.getName());
        assertEquals("This file should be selected !", true, selected);
    }

    /**
     * Test unwanted selection of a non source file.
     */
    @Test
    public void testUnwantedFile() {
        LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(
                LanguageRegistry.getLanguage(DummyLanguageModule.NAME));

        File javaFile = new File("/path/to/myFile.txt");

        boolean selected = fileSelector.accept(javaFile.getParentFile(), javaFile.getName());
        assertEquals("Not-source file must not be selected!", false, selected);
    }

    /**
     * Test unwanted selection of a java file.
     */
    @Test
    public void testUnwantedJavaFile() {
        LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(
                LanguageRegistry.getLanguage(DummyLanguageModule.NAME));

        File javaFile = new File("/path/to/MyClass.java");

        boolean selected = fileSelector.accept(javaFile.getParentFile(), javaFile.getName());
        assertEquals("Unwanted java file must not be selected!", false, selected);
    }
}
