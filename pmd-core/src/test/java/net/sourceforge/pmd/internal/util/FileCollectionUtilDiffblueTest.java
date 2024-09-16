package net.sourceforge.pmd.internal.util;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;

import net.sourceforge.pmd.AbstractConfiguration;
import net.sourceforge.pmd.cpd.CPDConfiguration;

import net.sourceforge.pmd.lang.document.FileCollector;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class FileCollectionUtilDiffblueTest {
    /**
     * Method under test:
     * {@link FileCollectionUtil#collectFiles(AbstractConfiguration, FileCollector)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testCollectFiles() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.document.FileCollector.setCharset(java.nio.charset.Charset)" because "collector" is null
        //       at net.sourceforge.pmd.internal.util.FileCollectionUtil.collectFiles(FileCollectionUtil.java:75)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        AbstractConfiguration configuration = null;
        FileCollector collector = null;

        // Act
        FileCollectionUtil.collectFiles(configuration, collector);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link FileCollectionUtil#collectFiles(CPDConfiguration, FileCollector)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testCollectFiles2() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.document.FileCollector.setCharset(java.nio.charset.Charset)" because "collector" is null
        //       at net.sourceforge.pmd.internal.util.FileCollectionUtil.collectFiles(FileCollectionUtil.java:75)
        //       at net.sourceforge.pmd.internal.util.FileCollectionUtil.collectFiles(FileCollectionUtil.java:70)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        CPDConfiguration cpdConfiguration = null;
        FileCollector collector = null;

        // Act
        FileCollectionUtil.collectFiles(cpdConfiguration, collector);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link FileCollectionUtil#collectFiles(FileCollector, List)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testCollectFiles3() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        FileCollector collector = null;
        List<Path> filePaths = null;

        // Act
        FileCollectionUtil.collectFiles(collector, filePaths);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link FileCollectionUtil#collectFileList(FileCollector, Path)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testCollectFileList() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.document.FileCollector.getReporter()" because "collector" is null
        //       at net.sourceforge.pmd.internal.util.FileCollectionUtil.collectFileList(FileCollectionUtil.java:122)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        FileCollector collector = null;
        Path fileList = null;

        // Act
        FileCollectionUtil.collectFileList(collector, fileList);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link FileCollectionUtil#collectDB(FileCollector, URI)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testCollectDB() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.document.FileCollector.getReporter()" because "collector" is null
        //       at net.sourceforge.pmd.internal.util.FileCollectionUtil.collectDB(FileCollectionUtil.java:180)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        FileCollector collector = null;
        URI uri = null;

        // Act
        FileCollectionUtil.collectDB(collector, uri);

        // Assert
        // TODO: Add assertions on result
    }
}
