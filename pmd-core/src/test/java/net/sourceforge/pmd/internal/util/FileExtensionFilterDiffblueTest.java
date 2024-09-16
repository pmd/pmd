package net.sourceforge.pmd.internal.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FileExtensionFilterDiffblueTest {
    /**
     * Method under test:
     * {@link FileExtensionFilter#FileExtensionFilter(boolean, String[])}
     */
    @Test
    void testNewFileExtensionFilter() {
        // Arrange
        String[] extensions = new String[]{"Extensions"};

        // Act and Assert
        assertFalse((new FileExtensionFilter(true, extensions)).test("Path"));
        assertArrayEquals(new String[]{"EXTENSIONS"}, extensions);
    }

    /**
     * Method under test:
     * {@link FileExtensionFilter#FileExtensionFilter(boolean, String[])}
     */
    @Test
    void testNewFileExtensionFilter2() {
        // Arrange
        String[] extensions = new String[]{"Extensions"};

        // Act and Assert
        assertFalse((new FileExtensionFilter(false, extensions)).test("Path"));
        assertArrayEquals(new String[]{"Extensions"}, extensions);
    }

    /**
     * Method under test:
     * {@link FileExtensionFilter#FileExtensionFilter(boolean, String[])}
     */
    @Test
    void testNewFileExtensionFilter3() {
        // Arrange, Act and Assert
        assertFalse((new FileExtensionFilter(true, "42")).test("Path"));
    }

    /**
     * Method under test:
     * {@link FileExtensionFilter#FileExtensionFilter(boolean, String[])}
     */
    @Test
    void testNewFileExtensionFilter4() {
        // Arrange, Act and Assert
        assertTrue((new FileExtensionFilter(true, "")).test("Path"));
    }

    /**
     * Method under test:
     * {@link FileExtensionFilter#FileExtensionFilter(boolean, String[])}
     */
    @Test
    void testNewFileExtensionFilter5() {
        // Arrange
        String[] extensions = new String[]{"Extensions"};

        // Act and Assert
        assertFalse((new FileExtensionFilter(true, extensions)).test(null));
        assertArrayEquals(new String[]{"EXTENSIONS"}, extensions);
    }

    /**
     * Method under test:
     * {@link FileExtensionFilter#FileExtensionFilter(boolean, String[])}
     */
    @Test
    void testNewFileExtensionFilter6() {
        // Arrange, Act and Assert
        assertTrue((new FileExtensionFilter(false, null)).test(null));
    }

    /**
     * Method under test: {@link FileExtensionFilter#test(String)}
     */
    @Test
    void testTest() {
        // Arrange, Act and Assert
        assertFalse((new FileExtensionFilter(true, "Extensions")).test("Path"));
        assertFalse((new FileExtensionFilter(false, "Extensions")).test("Path"));
        assertFalse((new FileExtensionFilter(true, "Extensions")).test(null));
        assertTrue((new FileExtensionFilter(false, null)).test("Path"));
    }
}
