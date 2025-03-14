/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static net.sourceforge.pmd.internal.util.FileCollectionUtil.collectFileList;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.internal.util.FileCollectionUtil;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * @author Clément Fournier
 */
class FileCollectorTest {
    private static final Path RESOURCES = Paths.get("src/test/resources/net/sourceforge/pmd/lang/document/filecollectortest/");

    @TempDir
    private Path tempFolder;

    @Test
    void testAddFile() throws IOException {
        Path foo = newFile(tempFolder, "foo.dummy");
        Path bar = newFile(tempFolder, "bar.unknown");

        FileCollector collector = newCollector();

        assertTrue(collector.addFile(foo), "should be dummy language");
        assertFalse(collector.addFile(bar), "should be unknown language");

        assertCollected(collector, listOf(FileId.fromPath(foo)));
    }

    @Test
    void testAddFileForceLanguage() throws IOException {
        Path bar = newFile(tempFolder, "bar.unknown");

        Language dummy = DummyLanguageModule.getInstance();

        FileCollector collector = newCollector(dummy.getDefaultVersion());

        assertTrue(collector.addFile(bar, dummy), "should be unknown language");
        assertCollected(collector, listOf(FileId.fromPath(bar)));
        assertNoErrors(collector);
    }

    @Test
    void testAddFileNotExists() {
        FileCollector collector = newCollector();

        assertFalse(collector.addFile(tempFolder.resolve("does_not_exist.dummy")));
        assertEquals(1, collector.getReporter().numErrors());
    }

    @Test
    void testAddFileNotAFile() throws IOException {
        Path dir = tempFolder.resolve("src");
        Files.createDirectories(dir);

        FileCollector collector = newCollector();
        assertFalse(collector.addFile(dir));
        assertEquals(1, collector.getReporter().numErrors());
    }

    @Test
    void testAddDirectory() throws IOException {
        Path root = tempFolder;
        Path foo = newFile(root, "src/foo.dummy");
        newFile(root, "src/bar.unknown");
        Path bar = newFile(root, "src/x/bar.dummy");

        FileCollector collector = newCollector();

        collector.addDirectory(root.resolve("src"));

        assertCollected(collector, listOf(FileId.fromPath(foo), FileId.fromPath(bar)));
    }

    @Test
    void testGetApplicableFiles() {
        FileCollector collector = newCollector();

        collectFileList(collector, RESOURCES.resolve("filelist.txt"));

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        assertThat(applicableFiles, hasSize(2));
        assertThat(applicableFiles.get(0).getFileId().getFileName(), equalTo("anotherfile.dummy"));
        assertThat(applicableFiles.get(1).getFileId().getFileName(), equalTo("somefile.dummy"));
    }

    @Test
    void testGetApplicableFilesMultipleLines() {
        FileCollector collector = newCollector();

        collectFileList(collector, RESOURCES.resolve("filelist2.txt"));

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        // note: the file has 3 entries, but one is duplicated, resulting in 2 individual files
        assertThat(applicableFiles, hasSize(2));
        assertFilenameIs(applicableFiles.get(0), "anotherfile.dummy");
        assertFilenameIs(applicableFiles.get(1), "somefile.dummy");
    }

    @Test
    void testGetApplicableFilesWithIgnores() {
        FileCollector collector = newCollector();

        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setInputFilePath(RESOURCES.resolve("filelist3.txt"));
        configuration.setIgnoreFilePath(RESOURCES.resolve("ignorelist.txt"));
        FileCollectionUtil.collectFiles(configuration, collector);

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        assertThat(applicableFiles, hasSize(2));
        assertFilenameIs(applicableFiles.get(0), "somefile2.dummy");
        assertFilenameIs(applicableFiles.get(1), "somefile4.dummy");
    }

    @Test
    void testRelativizeWith() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.setInputFilePath(RESOURCES.resolve("filelist2.txt"));
        conf.addRelativizeRoot(Paths.get("src/test/resources"));
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(2));
            assertHasName(files.get(0), IOUtil.normalizePath("net/sourceforge/pmd/lang/document/filecollectortest/src/anotherfile.dummy"), pmd);
            assertHasName(files.get(1), IOUtil.normalizePath("net/sourceforge/pmd/lang/document/filecollectortest/src/somefile.dummy"), pmd);
        }
    }

    @Test
    void testRelativizeWithOtherDir() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.setInputFilePath(RESOURCES.resolve("filelist4.txt"));
        conf.addRelativizeRoot(RESOURCES.resolve("src"));
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertHasName(files.get(0), ".." + IOUtil.normalizePath("/otherSrc/somefile.dummy"), pmd);
            assertHasName(files.get(1), "anotherfile.dummy", pmd);
            assertHasName(files.get(2), "somefile.dummy", pmd);
        }
    }

    @Test
    void testRelativizeWithSeveralDirs() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.setInputFilePath(RESOURCES.resolve("filelist4.txt"));
        conf.addRelativizeRoot(RESOURCES.resolve("src"));
        conf.addRelativizeRoot(RESOURCES.resolve("otherSrc"));
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertHasName(files.get(0), "somefile.dummy", pmd);
            assertHasName(files.get(1), "anotherfile.dummy", pmd);
            assertHasName(files.get(2), "somefile.dummy", pmd);
        }
    }

    @Test
    void testUseAbsolutePaths() {
        PMDConfiguration conf = new PMDConfiguration();
        conf.setInputFilePath(RESOURCES.resolve("filelist4.txt"));
        conf.addRelativizeRoot(RESOURCES.toAbsolutePath().getRoot());
        try (PmdAnalysis pmd = PmdAnalysis.create(conf)) {
            List<TextFile> files = pmd.files().getCollectedFiles();
            assertThat(files, hasSize(3));
            assertHasName(files.get(0), RESOURCES.resolve("otherSrc/somefile.dummy").toAbsolutePath().toString(), pmd);
            assertHasName(files.get(1), RESOURCES.resolve("src/anotherfile.dummy").toAbsolutePath().toString(), pmd);
            assertHasName(files.get(2), RESOURCES.resolve("src/somefile.dummy").toAbsolutePath().toString(), pmd);
        }
    }


    @Test
    void testGetApplicableFilesWithDirAndIgnores() {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.addInputPath(RESOURCES.resolve("src"));
        configuration.setIgnoreFilePath(RESOURCES.resolve("ignorelist.txt"));

        FileCollector collector = newCollector();
        FileCollectionUtil.collectFiles(configuration, collector);

        List<TextFile> applicableFiles = collector.getCollectedFiles();
        assertThat(applicableFiles, hasSize(4));
        assertFilenameIs(applicableFiles.get(0), "anotherfile.dummy");
        assertFilenameIs(applicableFiles.get(1), "somefile.dummy");
        assertFilenameIs(applicableFiles.get(2), "somefile2.dummy");
        assertFilenameIs(applicableFiles.get(3), "somefile4.dummy");
    }

    public static void assertHasName(TextFile textFile, String expected, PmdAnalysis pmd) {
        assertThat(pmd.fileNameRenderer().getDisplayName(textFile), equalTo(expected));
    }

    private static void assertFilenameIs(TextFile textFile, String suffix) {
        assertThat(textFile.getFileId().getFileName(), is(suffix));
    }

    private Path newFile(Path root, String path) throws IOException {
        Path resolved = root.resolve(path);
        Files.createDirectories(resolved.getParent());
        Files.createFile(resolved);
        return resolved;
    }

    private void assertCollected(FileCollector collector, List<FileId> expected) {
        List<FileId> actual = CollectionUtil.map(collector.getCollectedFiles(), TextFile::getFileId);
        assertEquals(expected, actual);
    }

    private void assertNoErrors(FileCollector collector) {
        assertEquals(0, collector.getReporter().numErrors(), "No errors expected");
    }

    private FileCollector newCollector() {
        return newCollector(null);
    }

    private FileCollector newCollector(LanguageVersion forcedVersion) {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer(LanguageRegistry.PMD, forcedVersion);
        return FileCollector.newCollector(discoverer, new TestMessageReporter());
    }
}
