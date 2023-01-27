/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static net.sourceforge.pmd.PmdCoreTestUtils.dummyVersion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;

class TextFilesTest {

    @TempDir
    Path tempDir;

    @Test
    void testNioFile() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some content");
        try (TextFile tf = TextFile.forPath(file, StandardCharsets.UTF_8, dummyVersion())) {
            assertEquals(file.toAbsolutePath().toUri().toString(), tf.getPathId());
            assertEquals(file.toString(), tf.getDisplayName());
            assertEquals(dummyVersion(), tf.getLanguageVersion());
            assertEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    void testEquals() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some content").toAbsolutePath();
        try (TextFile tf = TextFile.forPath(file, StandardCharsets.UTF_8, dummyVersion())) {
            try (TextFile tfPrime = TextFile.forPath(file, StandardCharsets.UTF_8, dummyVersion())) {
                try (TextFile stringTf = TextFile.forCharSeq("some content", file.toUri().toString(), dummyVersion())) {
                    assertEquals(tf.getPathId(), stringTf.getPathId());

                    // despite same path id, they are different implementations
                    assertNotEquals(tf, stringTf);
                    assertNotEquals(stringTf, tf);

                    // identical, but string text files use identity
                    assertNotEquals(stringTf, TextFile.forCharSeq("some content", file.toString(), dummyVersion()));

                    // those are identical so are equals
                    assertNotSame(tf, tfPrime);
                    assertEquals(tf, tfPrime);
                    assertEquals(tfPrime, tf);
                    assertEquals(tf.hashCode(), tfPrime.hashCode());
                }
            }
        }
    }

    @Test
    void testStringDataSourceCompat() throws IOException {
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
    void testFileDataSourceCompat() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some content");

        DataSource ds = new FileDataSource(file.toFile());
        PMDConfiguration config = new PMDConfiguration();
        config.setForceLanguageVersion(DummyLanguageModule.getInstance().getDefaultVersion());
        try (TextFile tf = TextFile.dataSourceCompat(ds, config)) {
            assertEquals(ds.getNiceFileName(false, null), tf.getPathId());
            assertEquals(ds.getNiceFileName(false, null), tf.getDisplayName());
            assertEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    void testFileDataSourceCompatWithEncoding() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_16BE, "some content");

        DataSource ds = new FileDataSource(file.toFile());
        PMDConfiguration config = new PMDConfiguration();
        config.setForceLanguageVersion(DummyLanguageModule.getInstance().getDefaultVersion());
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
    void testNioFileWrite() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some content");
        try (TextFile tf = TextFile.forPath(file, StandardCharsets.UTF_8, dummyVersion())) {
            assertEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
            assertFalse(tf.isReadOnly(), "readonly");

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
    void testNioFileExplicitReadOnly() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some content");
        try (TextFile tf = TextFile.builderForPath(file, StandardCharsets.UTF_8, dummyVersion())
                                   .withDisplayName(file.toString())
                                   .asReadOnly().build()) {
            assertTrue(tf.isReadOnly(), "readonly");

            assertThrows(ReadOnlyFileException.class, () -> tf.writeContents(
                TextFileContent.fromCharSeq("new content")
            ));
        }
    }

    @Test
    void testNioFileCanBeReadMultipleTimes() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some content");
        try (TextFile tf = TextFile.forPath(file, StandardCharsets.UTF_8, dummyVersion())) {
            assertEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
            assertEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    void testNioFileBuilder() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some content");
        try (TextFile tf = TextFile.builderForPath(file, StandardCharsets.UTF_8, dummyVersion())
                                   .withDisplayName("aname")
                                   .build()) {
            assertEquals(file.toAbsolutePath().toUri().toString(), tf.getPathId());
            assertEquals("aname", tf.getDisplayName());
            assertEquals(dummyVersion(), tf.getLanguageVersion());
            assertEquals(Chars.wrap("some content"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    void testNioFileEscape() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some\r\ncontent");
        try (TextFile tf = TextFile.forPath(file, StandardCharsets.UTF_8, dummyVersion())) {
            assertEquals(Chars.wrap("some\ncontent"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    void testReaderFile() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some\r\ncontent");
        try (TextFile tf = TextFile.forReader(Files.newBufferedReader(file, StandardCharsets.UTF_8), "filename", dummyVersion())) {
            assertEquals("filename", tf.getPathId());
            assertEquals("filename", tf.getDisplayName());
            assertEquals(dummyVersion(), tf.getLanguageVersion());
            assertEquals(Chars.wrap("some\ncontent"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    void testReaderFileIsReadOnly() throws IOException {
        Path file = makeTmpFile(StandardCharsets.UTF_8, "some\r\ncontent");
        try (TextFile tf = TextFile.forReader(Files.newBufferedReader(file, StandardCharsets.UTF_8), "filename", dummyVersion())) {
            assertTrue(tf.isReadOnly(), "readonly");
            assertThrows(ReadOnlyFileException.class, () -> tf.writeContents(
                TextFileContent.fromCharSeq("new content")
            ));
        }
    }

    @Test
    void testStringFileEscape() throws IOException {
        try (TextFile tf = TextFile.forCharSeq("cont\r\nents", "filename", dummyVersion())) {
            assertEquals("filename", tf.getPathId());
            assertEquals("filename", tf.getDisplayName());
            assertEquals(dummyVersion(), tf.getLanguageVersion());
            assertEquals(Chars.wrap("cont\nents"), tf.readContents().getNormalizedText());
            assertThrows(ReadOnlyFileException.class, () -> tf.writeContents(
                TextFileContent.fromCharSeq("new content")
            ));
        }
    }

    @Test
    void testStringFileCanBeReadMultipleTimes() throws IOException {
        try (TextFile tf = TextFile.forCharSeq("contents", "filename", dummyVersion())) {
            assertEquals(Chars.wrap("contents"), tf.readContents().getNormalizedText());
            assertEquals(Chars.wrap("contents"), tf.readContents().getNormalizedText());
            assertEquals(Chars.wrap("contents"), tf.readContents().getNormalizedText());
        }
    }

    @Test
    void testStringFileIsReadonly() throws IOException {
        try (TextFile tf = TextFile.forCharSeq("contents", "filename", dummyVersion())) {
            assertTrue(tf.isReadOnly(), "readonly");
            assertThrows(ReadOnlyFileException.class, () -> tf.writeContents(
                TextFileContent.fromCharSeq("new content")
            ));
        }
    }

    private @NonNull Path makeTmpFile(Charset charset, String content) throws IOException {
        Path file = Files.createTempFile(tempDir, null, null);
        try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
            writer.write(content);
        }
        return file;
    }

}
