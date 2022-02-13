/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.util.document.FileCollector.FileWithLanguage;

/**
 * @author Clément Fournier
 */
public class FileCollectorTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    @Rule
    public ExpectedException exception = ExpectedException.none();

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

        FileCollector collector = newCollector();

        assertFalse("should be unknown language", collector.addFile(bar));
        assertCollected(collector, Collections.<String>emptyList());
        assertTrue("should be unknown language", collector.addFile(bar, dummy));
        assertCollected(collector, listOf("bar.unknown"));
    }

    @Test
    public void testAddFileNotExists() {
        Path root = tempFolder.getRoot().toPath();

        FileCollector collector = newCollector();

        exception.expect(IllegalArgumentException.class);
        collector.addFile(root.resolve("does_not_exist.dummy"));
    }

    @Test
    public void testAddFileNotAFile() throws IOException {
        Path root = tempFolder.getRoot().toPath();
        Path dir = root.resolve("src");
        Files.createDirectories(dir);

        FileCollector collector = newCollector();

        exception.expect(IllegalArgumentException.class);
        collector.addFile(dir);
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

    private FileCollector newCollector() {
        return new FileCollector(new LanguageVersionDiscoverer());
    }

    private Path newFile(Path root, String path) throws IOException {
        Path resolved = root.resolve(path);
        Files.createDirectories(resolved.getParent());
        Files.createFile(resolved);
        return resolved;
    }

    private void assertCollected(FileCollector collector, List<String> relPaths) {
        Map<String, String> actual = new LinkedHashMap<>();
        for (FileWithLanguage file : collector.getAllFilesToProcess()) {
            String relPath = tempFolder.getRoot().toPath().relativize(file.getPath()).toString();
            actual.put(relPath, file.getLanguage().getTerseName());
        }

        assertEquals(new ArrayList<>(actual.keySet()), relPaths);
    }
}
