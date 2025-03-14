/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageFilenameFilter;

/**
 * Tests on FileSelector.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
class FileSelectorTest {

    /**
     * Test wanted selection of a source file.
     */
    @Test
    void testWantedFile() {
        LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(DummyLanguageModule.getInstance());

        File javaFile = new File("/path/to/myFile.dummy");

        boolean selected = fileSelector.accept(javaFile.getParentFile(), javaFile.getName());
        assertTrue(selected, "This file should be selected !");
    }

    /**
     * Test unwanted selection of a non source file.
     */
    @Test
    void testUnwantedFile() {
        LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(DummyLanguageModule.getInstance());

        File javaFile = new File("/path/to/myFile.notdummy");

        boolean selected = fileSelector.accept(javaFile.getParentFile(), javaFile.getName());
        assertFalse(selected, "Not-source file must not be selected!");
    }

    /**
     * Test unwanted selection of a java file.
     */
    @Test
    void testUnwantedJavaFile() {
        LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(DummyLanguageModule.getInstance());

        File javaFile = new File("/path/to/MyClass.java");

        boolean selected = fileSelector.accept(javaFile.getParentFile(), javaFile.getName());
        assertFalse(selected, "Unwanted java file must not be selected!");
    }
}
