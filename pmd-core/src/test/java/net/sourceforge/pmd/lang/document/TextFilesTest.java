/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;

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
    public void testEquals() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some content").toAbsolutePath();
        try (TextFile tf = TextFile.forPath(file, StandardCharsets.UTF_8, dummyVersion)) {
            try (TextFile tf2 = TextFile.forCharSeq("some content", file.toString(), dummyVersion)) {
                assertEquals(tf.getPathId(), tf2.getPathId());

                assertNotEquals(tf, tf2);
                assertNotEquals(tf2, tf);
                assertEquals(tf, tf);
                assertEquals(tf, TextFile.forPath(file, StandardCharsets.UTF_8, dummyVersion));
            }
        }
    }

    @Test
    public void testStringDataSourceCompat() throws IOException {
        DataSource ds = DataSource.forString("text", "filename.dummy");
        PMDConfiguration config = new PMDConfiguration();
        try (TextFile tf = TextFile.dataSourceCompat(ds, config)) {
            assertEquals("filename.dummy", tf.getPathId());
            assertEquals("filename.dummy", tf.getDisplayName());
            assertEquals(DummyLanguageModule.getInstance().getDefaultVersion(), tf.getLanguageVersion());
            assertEquals(Chars.wrap("text"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    public void testFileDataSourceCompat() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some content");

        DataSource ds = new FileDataSource(file.toFile());
        PMDConfiguration config = new PMDConfiguration();
        try (TextFile tf = TextFile.dataSourceCompat(ds, config)) {
            assertEquals(ds.getNiceFileName(false, null), tf.getPathId());
            assertEquals(ds.getNiceFileName(false, null), tf.getDisplayName());
            assertEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    public void testFileDataSourceCompatWithEncoding() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_16BE, "some content");

        DataSource ds = new FileDataSource(file.toFile());
        PMDConfiguration config = new PMDConfiguration();
        config.setSourceEncoding(StandardCharsets.UTF_16BE.name());
        try (TextFile tf = TextFile.dataSourceCompat(ds, config)) {
            assertEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
        }

        // different encoding to produce garbage, to make sure encoding is used
        config.setSourceEncoding(StandardCharsets.UTF_16LE.name());
        try (TextFile tf = TextFile.dataSourceCompat(ds, config)) {
            assertNotEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
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
