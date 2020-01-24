/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileDataSourceTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private static final String SOMEFILE_DIR = "path/";
    private static final String SOMEFILE_TXT = "somefile.txt";
    private static final String SOMEFILE_TXT_FULL_PATH = SOMEFILE_DIR + SOMEFILE_TXT;

    private FileDataSource ds;
    private File someFile;
    private File someFolder;

    @Before
    public void setup() throws IOException {
        someFolder = tempFolder.newFolder(SOMEFILE_DIR);
        someFile = tempFolder.newFile(SOMEFILE_TXT_FULL_PATH);
        ds = new FileDataSource(someFile);
    }

    @Test
    public void testShortNamesSingleFile() {
        assertEquals(SOMEFILE_TXT, ds.getNiceFileName(true, someFile.getAbsolutePath()));
    }

    @Test
    public void testShortNamesSingleDir() {
        assertEquals(SOMEFILE_TXT, ds.getNiceFileName(true, someFolder.getAbsolutePath()));
    }

    @Test
    public void testShortNamesNullBase() {
        assertEquals(SOMEFILE_TXT, ds.getNiceFileName(true, null));
    }

    @Test
    public void testShortNamesCommaSeparatedDirs() {
        // use 2 dirs, one relative (similar, but not resolving to the same location) and one absolute
        assertEquals(SOMEFILE_TXT, ds.getNiceFileName(true, SOMEFILE_DIR + "," + someFolder.getAbsolutePath()));
    }

    @Test
    public void testShortNamesCommaSeparatedFiles() {
        // use 2 files, one relative (similar, but not resolving to the same location) and one absolute
        assertEquals(SOMEFILE_TXT, ds.getNiceFileName(true, SOMEFILE_TXT_FULL_PATH + "," + someFile.getAbsolutePath()));
    }

    @Test
    public void testShortNamesCommaSeparatedMixed() {
        // use a file and a dir, one relative (similar, but not resolving to the same location) and one absolute
        assertEquals(SOMEFILE_TXT, ds.getNiceFileName(true, SOMEFILE_TXT_FULL_PATH + "," + someFolder.getAbsolutePath()));
    }

    @Test
    public void testLongNamesSingleFile() throws IOException {
        assertEquals(someFile.getCanonicalFile().getAbsolutePath(), ds.getNiceFileName(false, someFile.getAbsolutePath()));
    }

    @Test
    public void testLongNamesSingleDir() throws IOException {
        assertEquals(someFile.getCanonicalFile().getAbsolutePath(), ds.getNiceFileName(false, someFolder.getAbsolutePath()));
    }

    @Test
    public void testLongNamesNullBase() throws IOException {
        assertEquals(someFile.getCanonicalFile().getAbsolutePath(), ds.getNiceFileName(false, null));
    }

    @Test
    public void testLongNamesCommaSeparatedDirs() throws IOException {
        // use 2 dirs, one relative (similar, but not resolving to the same location) and one absolute
        assertEquals(someFile.getCanonicalFile().getAbsolutePath(),
                ds.getNiceFileName(false, SOMEFILE_DIR + "," + someFolder.getAbsolutePath()));
    }

    @Test
    public void testLongNamesCommaSeparatedFiles() throws IOException {
        // use 2 files, one relative (similar, but not resolving to the same location) and one absolute
        assertEquals(someFile.getCanonicalFile().getAbsolutePath(),
                ds.getNiceFileName(false, SOMEFILE_TXT_FULL_PATH + "," + someFile.getAbsolutePath()));
    }

    @Test
    public void testLongNamesCommaSeparatedMixed() throws IOException {
        // use a file and a dir, one relative (similar, but not resolving to the same location) and one absolute
        assertEquals(someFile.getCanonicalFile().getAbsolutePath(),
                ds.getNiceFileName(false, SOMEFILE_TXT_FULL_PATH + "," + someFolder.getAbsolutePath()));
    }
}
