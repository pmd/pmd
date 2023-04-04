/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;

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

        assertCollected(collector, listOf("foo.dummy"));
    }

    @Test
    void testAddFileForceLanguage() throws IOException {
        Path bar = newFile(tempFolder, "bar.unknown");

        Language dummy = DummyLanguageModule.getInstance();

        FileCollector collector = newCollector(dummy.getDefaultVersion());

        assertTrue(collector.addFile(bar, dummy), "should be unknown language");
        assertCollected(collector, listOf("bar.unknown"));
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
        newFile(root, "src/foo.dummy");
        newFile(root, "src/bar.unknown");
        newFile(root, "src/x/bar.dummy");

        FileCollector collector = newCollector();

        collector.addDirectory(root.resolve("src"));

        assertCollected(collector, listOf("src/foo.dummy", "src/x/bar.dummy"));
    }

    @Test
    void testRelativizeLegacy() {
        String displayName = FileCollector.getDisplayNameLegacy(Paths.get("a", "b", "c"), listOf(Paths.get("a").toString()));
        assertEquals(displayName, Paths.get("b", "c").toString());
    }

    @Test
    void testRelativize() {
        String displayName = FileCollector.getDisplayName(Paths.get("a", "b", "c"), listOf(Paths.get("a")));
        assertEquals(displayName, Paths.get("b", "c").toString());
    }

    private Path newFile(Path root, String path) throws IOException {
        Path resolved = root.resolve(path);
        Files.createDirectories(resolved.getParent());
        Files.createFile(resolved);
        return resolved;
    }

    private void assertCollected(FileCollector collector, List<String> relPaths) {
        Map<String, String> actual = new LinkedHashMap<>();
        for (TextFile file : collector.getCollectedFiles()) {
            actual.put(file.getDisplayName(), file.getLanguageVersion().getTerseName());
        }

        relPaths = new ArrayList<>(relPaths);
        for (int i = 0; i < relPaths.size(); i++) {
            // normalize, we want display names to be platform-specific
            relPaths.set(i, relPaths.get(i).replace('/', File.separatorChar));
        }

        assertEquals(relPaths, new ArrayList<>(actual.keySet()));
    }

    private void assertNoErrors(FileCollector collector) {
        assertEquals(0, collector.getReporter().numErrors(), "No errors expected");
    }

    private FileCollector newCollector() {
        return newCollector(null);
    }

    private FileCollector newCollector(LanguageVersion forcedVersion) {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer(LanguageRegistry.PMD, forcedVersion);
        FileCollector collector = FileCollector.newCollector(discoverer, new TestMessageReporter());
        collector.relativizeWith(tempFolder.toAbsolutePath().toString());
        return collector;
    }
}
