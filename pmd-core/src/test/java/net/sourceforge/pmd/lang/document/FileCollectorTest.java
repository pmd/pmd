/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;

/**
 * @author Clément Fournier
 */
public class FileCollectorTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testAddFile() throws IOException {
        Path root = tempFolder.getRoot().toPath();
        Path foo = newFile(root, "foo.dummy");
        Path bar = newFile(root, "bar.unknown");

        FileCollector collector = newCollector();

        assertTrue("should be dummy language", collector.addFile(foo));
        assertFalse("should be unknown language", collector.addFile(bar));

        assertCollected(collector, listOf("foo.dummy"));
    }

    @Test
    public void testAddFileForceLanguage() throws IOException {
        Path root = tempFolder.getRoot().toPath();
        Path bar = newFile(root, "bar.unknown");

        Language dummy = LanguageRegistry.findLanguageByTerseName("dummy");

        FileCollector collector = newCollector(dummy.getDefaultVersion());

        assertTrue("should be unknown language", collector.addFile(bar, dummy));
        assertCollected(collector, listOf("bar.unknown"));
        assertNoErrors(collector);
    }

    @Test
    public void testAddFileNotExists() {
        Path root = tempFolder.getRoot().toPath();

        FileCollector collector = newCollector();

        assertFalse(collector.addFile(root.resolve("does_not_exist.dummy")));
        assertEquals(1, collector.getReporter().numErrors());
    }

    @Test
    public void testAddFileNotAFile() throws IOException {
        Path root = tempFolder.getRoot().toPath();
        Path dir = root.resolve("src");
        Files.createDirectories(dir);

        FileCollector collector = newCollector();
        assertFalse(collector.addFile(dir));
        assertEquals(1, collector.getReporter().numErrors());
    }

    @Test
    public void testAddDirectory() throws IOException {
        Path root = tempFolder.getRoot().toPath();
        newFile(root, "src/foo.dummy");
        newFile(root, "src/bar.unknown");
        newFile(root, "src/x/bar.dummy");

        FileCollector collector = newCollector();

        collector.addDirectory(root.resolve("src"));

        assertCollected(collector, listOf("src/foo.dummy", "src/x/bar.dummy"));
    }

    @Test
    public void testRelativize() throws IOException {
        String displayName = FileCollector.getDisplayNameLegacy(Paths.get("a", "b", "c"), listOf(Paths.get("a").toString()));
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

        for (int i = 0; i < relPaths.size(); i++) {
            // normalize, we want display names to be platform-specific
            relPaths.set(i, relPaths.get(i).replace('/', File.separatorChar));
        }

        assertEquals(relPaths, new ArrayList<>(actual.keySet()));
    }

    private void assertNoErrors(FileCollector collector) {
        assertEquals("No errors expected", 0, collector.getReporter().numErrors());
    }

    private FileCollector newCollector() {
        return newCollector(null);
    }

    private FileCollector newCollector(LanguageVersion forcedVersion) {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer(forcedVersion);
        FileCollector collector = FileCollector.newCollector(discoverer, new TestMessageReporter());
        collector.relativizeWith(tempFolder.getRoot().getAbsolutePath());
        return collector;
    }
}
