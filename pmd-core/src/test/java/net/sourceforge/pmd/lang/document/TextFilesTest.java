/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageVersion;

public class TextFilesTest {

    @Rule
    public TemporaryFolder tempDir = TemporaryFolder.builder().build();

    private LanguageVersion dummyVersion = DummyLanguageModule.getInstance().getDefaultVersion();

    @Test
    public void testNioFile() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some content");
        try (TextFile tf = TextFile.forPath(file, StandardCharsets.UTF_8, dummyVersion)) {
            Assert.assertEquals(file.toAbsolutePath().toString(), tf.getPathId());
            Assert.assertEquals(file.toString(), tf.getDisplayName());
            Assert.assertEquals(dummyVersion, tf.getLanguageVersion());
            Assert.assertEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    public void testNioFileBuilder() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some content");
        try (TextFile tf = TextFile.builderForPath(file, StandardCharsets.UTF_8, dummyVersion)
                                   .withDisplayName("aname")
                                   .build()) {
            Assert.assertEquals(file.toAbsolutePath().toString(), tf.getPathId());
            Assert.assertEquals("aname", tf.getDisplayName());
            Assert.assertEquals(dummyVersion, tf.getLanguageVersion());
            Assert.assertEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    public void testNioFileEscape() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some\r\ncontent");
        try (TextFile tf = TextFile.forPath(file, StandardCharsets.UTF_8, dummyVersion)) {
            Assert.assertEquals(Chars.wrap("some\ncontent"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    public void testReaderFileEscape() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some\r\ncontent");
        try (TextFile tf = TextFile.forReader(Files.newBufferedReader(file, StandardCharsets.UTF_8), "filename", dummyVersion)) {
            Assert.assertEquals("filename", tf.getPathId());
            Assert.assertEquals("filename", tf.getDisplayName());
            Assert.assertEquals(dummyVersion, tf.getLanguageVersion());
            Assert.assertEquals(Chars.wrap("some\ncontent"), tf.readContents().getNormalizedText());
            Assert.assertThrows(ReadOnlyFileException.class, () -> tf.writeContents(
                TextFileContent.fromCharSeq("new content")
            ));
        }
    }@Test
    public void testStringFileEscape() throws IOException {
        try (TextFile tf = TextFile.forCharSeq("cont\r\nents", "filename", dummyVersion)) {
            Assert.assertEquals("filename", tf.getPathId());
            Assert.assertEquals("filename", tf.getDisplayName());
            Assert.assertEquals(dummyVersion, tf.getLanguageVersion());
            Assert.assertEquals(Chars.wrap("cont\r\nent"), tf.readContents().getNormalizedText());
            Assert.assertThrows(ReadOnlyFileException.class, () -> tf.writeContents(
                TextFileContent.fromCharSeq("new content")
            ));
        }
    }

    private @NonNull Path makeTmpFile(Charset charset, String content) throws IOException {
        Path file = tempDir.newFile().toPath();
        try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
            IOUtils.write(content, writer);
        }
        return file;
    }

}
