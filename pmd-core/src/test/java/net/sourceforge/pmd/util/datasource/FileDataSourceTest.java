/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileDataSourceTest {

    @TempDir
    private Path tempFolder;

    private static final String SOMEFILE_DIR = "path/";
    private static final String SOMEFILE_TXT = "somefile.txt";
    private static final String SOMEFILE_TXT_FULL_PATH = SOMEFILE_DIR + SOMEFILE_TXT;

    private FileDataSource ds;
    private File someFile;
    private File someFolder;

    @BeforeEach
    void setup() throws IOException {
        someFolder = tempFolder.resolve(SOMEFILE_DIR).toFile();
        assertTrue(someFolder.mkdir());
        someFile = tempFolder.resolve(SOMEFILE_TXT_FULL_PATH).toFile();
        ds = new FileDataSource(someFile);
    }

    @Test
    void testShortNamesSingleFile() {
        assertEquals(SOMEFILE_TXT, ds.getNiceFileName(true, someFile.getAbsolutePath()));
    }

    @Test
    void testShortNamesSingleDir() {
        assertEquals(SOMEFILE_TXT, ds.getNiceFileName(true, someFolder.getAbsolutePath()));
    }

    @Test
    void testShortNamesNullBase() {
        assertEquals(SOMEFILE_TXT, ds.getNiceFileName(true, null));
    }

    @Test
    void testShortNamesCommaSeparatedDirs() {
        // use 2 dirs, one relative (similar, but not resolving to the same location) and one absolute
        assertEquals(SOMEFILE_TXT, ds.getNiceFileName(true, SOMEFILE_DIR + "," + someFolder.getAbsolutePath()));
    }

    @Test
    void testShortNamesCommaSeparatedFiles() {
        // use 2 files, one relative (similar, but not resolving to the same location) and one absolute
        assertEquals(SOMEFILE_TXT, ds.getNiceFileName(true, SOMEFILE_TXT_FULL_PATH + "," + someFile.getAbsolutePath()));
    }

    @Test
    void testShortNamesCommaSeparatedMixed() {
        // use a file and a dir, one relative (similar, but not resolving to the same location) and one absolute
        assertEquals(SOMEFILE_TXT, ds.getNiceFileName(true, SOMEFILE_TXT_FULL_PATH + "," + someFolder.getAbsolutePath()));
    }

    @Test
    void testLongNamesSingleFile() throws IOException {
        assertEquals(someFile.getCanonicalFile().getAbsolutePath(), ds.getNiceFileName(false, someFile.getAbsolutePath()));
    }

    @Test
    void testLongNamesSingleDir() throws IOException {
        assertEquals(someFile.getCanonicalFile().getAbsolutePath(), ds.getNiceFileName(false, someFolder.getAbsolutePath()));
    }

    @Test
    void testLongNamesNullBase() throws IOException {
        assertEquals(someFile.getCanonicalFile().getAbsolutePath(), ds.getNiceFileName(false, null));
    }

    @Test
    void testLongNamesCommaSeparatedDirs() throws IOException {
        // use 2 dirs, one relative (similar, but not resolving to the same location) and one absolute
        assertEquals(someFile.getCanonicalFile().getAbsolutePath(),
                ds.getNiceFileName(false, SOMEFILE_DIR + "," + someFolder.getAbsolutePath()));
    }

    @Test
    void testLongNamesCommaSeparatedFiles() throws IOException {
        // use 2 files, one relative (similar, but not resolving to the same location) and one absolute
        assertEquals(someFile.getCanonicalFile().getAbsolutePath(),
                ds.getNiceFileName(false, SOMEFILE_TXT_FULL_PATH + "," + someFile.getAbsolutePath()));
    }

    @Test
    void testLongNamesCommaSeparatedMixed() throws IOException {
        // use a file and a dir, one relative (similar, but not resolving to the same location) and one absolute
        assertEquals(someFile.getCanonicalFile().getAbsolutePath(),
                ds.getNiceFileName(false, SOMEFILE_TXT_FULL_PATH + "," + someFolder.getAbsolutePath()));
    }
}
