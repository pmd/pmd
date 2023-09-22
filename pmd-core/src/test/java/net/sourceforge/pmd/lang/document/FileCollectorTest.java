/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
