package net.sourceforge.pmd.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.Test;

class FileUtilDiffblueTest {
    /**
     * Method under test: {@link FileUtil#getFileNameWithoutExtension(String)}
     */
    @Test
    void testGetFileNameWithoutExtension() {
        // Arrange, Act and Assert
        assertEquals("foo", FileUtil.getFileNameWithoutExtension("foo.txt"));
        assertEquals("File Name", FileUtil.getFileNameWithoutExtension("File Name"));
    }

    /**
     * Method under test: {@link FileUtil#normalizeFilename(String)}
     */
    @Test
    void testNormalizeFilename() {
        // Arrange, Act and Assert
        assertEquals("foo.txt", FileUtil.normalizeFilename("foo.txt"));
        assertNull(FileUtil.normalizeFilename(null));
    }

    /**
     * Method under test: {@link FileUtil#toExistingPath(String)}
     */
    @Test
    void testToExistingPath() throws FileNotFoundException {
        // Arrange, Act and Assert
        assertThrows(FileNotFoundException.class, () -> FileUtil.toExistingPath("Root"));
    }

    /**
     * Method under test: {@link FileUtil#toExistingPath(String)}
     */
    @Test
    void testToExistingPath2() throws FileNotFoundException {
        // Arrange, Act and Assert
        File toFileResult = FileUtil.toExistingPath("").toFile();
        assertEquals("", toFileResult.getName());
        assertFalse(toFileResult.isAbsolute());
    }

    /**
     * Method under test: {@link FileUtil#findPatternInFile(File, String)}
     */
    @Test
    void testFindPatternInFile() {
        // Arrange, Act and Assert
        assertThrows(RuntimeException.class, () -> FileUtil
                .findPatternInFile(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toFile(), "Pattern"));
    }

    /**
     * Method under test: {@link FileUtil#readFilelistEntries(Path)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testReadFilelistEntries() throws IOException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.nio.file.NoSuchFileException: /var/folders/3n/jvrvqk3j49dfkqr89ss739540000gn/T/test.txt
        //       at java.base/sun.nio.fs.UnixException.translateToIOException(UnixException.java:92)
        //       at java.base/sun.nio.fs.UnixException.rethrowAsIOException(UnixException.java:106)
        //       at java.base/sun.nio.fs.UnixException.rethrowAsIOException(UnixException.java:111)
        //       at java.base/sun.nio.fs.UnixFileSystemProvider.newByteChannel(UnixFileSystemProvider.java:261)
        //       at java.base/java.nio.file.Files.newByteChannel(Files.java:379)
        //       at java.base/java.nio.file.Files.newByteChannel(Files.java:431)
        //       at java.base/java.nio.file.spi.FileSystemProvider.newInputStream(FileSystemProvider.java:420)
        //       at java.base/java.nio.file.Files.newInputStream(Files.java:159)
        //       at java.base/java.nio.file.Files.newBufferedReader(Files.java:2902)
        //       at java.base/java.nio.file.Files.readAllLines(Files.java:3397)
        //       at java.base/java.nio.file.Files.readAllLines(Files.java:3438)
        //       at net.sourceforge.pmd.internal.util.FileUtil.readFilelistEntries(FileUtil.java:116)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Path filelist = null;

        // Act
        List<Path> actualReadFilelistEntriesResult = FileUtil.readFilelistEntries(filelist);

        // Assert
        // TODO: Add assertions on result
    }
}
