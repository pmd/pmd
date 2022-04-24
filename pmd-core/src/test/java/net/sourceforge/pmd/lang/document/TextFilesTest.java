/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
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
            assertEquals(file.toAbsolutePath().toString(), tf.getPathId());
            assertEquals(file.toString(), tf.getDisplayName());
            assertEquals(dummyVersion, tf.getLanguageVersion());
            assertEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    public void testNioFileWrite() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some content");
        try (TextFile tf = TextFile.forPath(file, StandardCharsets.UTF_8, dummyVersion)) {
            assertEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
            assertFalse("readonly", tf.isReadOnly());

            // write with CRLF
            tf.writeContents(
                TextFileContent.fromCharSeq("new content\r\n")
            );

            TextFileContent read = tf.readContents();
            // is normalized to LF when rereading
            assertEquals(Chars.wrap("new content\n"), read.getNormalizedText());
            // but line terminator is detected as CRLF
            assertEquals("\r\n", read.getLineTerminator());

            tf.writeContents(
                TextFileContent.fromCharSeq("new content\n")
            );

            assertEquals(Chars.wrap("new content\n"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    public void testNioFileExplicitReadOnly() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some content");
        try (TextFile tf = TextFile.builderForPath(file, StandardCharsets.UTF_8, dummyVersion)
                                   .asReadOnly().build()) {
            assertTrue("readonly", tf.isReadOnly());

            assertThrows(ReadOnlyFileException.class, () -> tf.writeContents(
                TextFileContent.fromCharSeq("new content")
            ));
        }
    }

    @Test
    public void testNioFileCanBeReadMultipleTimes() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some content");
        try (TextFile tf = TextFile.forPath(file, StandardCharsets.UTF_8, dummyVersion)) {
            assertEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
            assertEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    public void testNioFileBuilder() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some content");
        try (TextFile tf = TextFile.builderForPath(file, StandardCharsets.UTF_8, dummyVersion)
                                   .withDisplayName("aname")
                                   .build()) {
            assertEquals(file.toAbsolutePath().toString(), tf.getPathId());
            assertEquals("aname", tf.getDisplayName());
            assertEquals(dummyVersion, tf.getLanguageVersion());
            assertEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    public void testNioFileEscape() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some\r\ncontent");
        try (TextFile tf = TextFile.forPath(file, StandardCharsets.UTF_8, dummyVersion)) {
            assertEquals(Chars.wrap("some\ncontent"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    public void testReaderFile() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some\r\ncontent");
        try (TextFile tf = TextFile.forReader(Files.newBufferedReader(file, StandardCharsets.UTF_8), "filename", dummyVersion)) {
            assertEquals("filename", tf.getPathId());
            assertEquals("filename", tf.getDisplayName());
            assertEquals(dummyVersion, tf.getLanguageVersion());
            assertEquals(Chars.wrap("some\ncontent"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    public void testReaderFileIsReadOnly() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some\r\ncontent");
        try (TextFile tf = TextFile.forReader(Files.newBufferedReader(file, StandardCharsets.UTF_8), "filename", dummyVersion)) {
            assertTrue("readonly", tf.isReadOnly());
            assertThrows(ReadOnlyFileException.class, () -> tf.writeContents(
                TextFileContent.fromCharSeq("new content")
            ));
        }
    }

    @Test
    public void testStringFileEscape() throws IOException {
        try (TextFile tf = TextFile.forCharSeq("cont\r\nents", "filename", dummyVersion)) {
            assertEquals("filename", tf.getPathId());
            assertEquals("filename", tf.getDisplayName());
            assertEquals(dummyVersion, tf.getLanguageVersion());
            assertEquals(Chars.wrap("cont\nents"), tf.readContents().getNormalizedText());
            assertThrows(ReadOnlyFileException.class, () -> tf.writeContents(
                TextFileContent.fromCharSeq("new content")
            ));
        }
    }

    @Test
    public void testStringFileCanBeReadMultipleTimes() throws IOException {
        try (TextFile tf = TextFile.forCharSeq("contents", "filename", dummyVersion)) {
            assertEquals(Chars.wrap("contents"), tf.readContents().getNormalizedText());
            assertEquals(Chars.wrap("contents"), tf.readContents().getNormalizedText());
            assertEquals(Chars.wrap("contents"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    public void testStringFileIsReadonly() throws IOException {
        try (TextFile tf = TextFile.forCharSeq("contents", "filename", dummyVersion)) {
            assertTrue("readonly", tf.isReadOnly());
            assertThrows(ReadOnlyFileException.class, () -> tf.writeContents(
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
