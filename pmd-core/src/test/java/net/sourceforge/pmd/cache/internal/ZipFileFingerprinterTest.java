/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;

class ZipFileFingerprinterTest extends AbstractClasspathEntryFingerprinterTest {

    @Test
    void zipEntryMetadataDoesNotAffectFingerprint() throws IOException {
        final Path file = createValidNonEmptyFile();
        final long baselineFingerprint = getBaseLineFingerprint(file);
        final long originalFileSize = Files.size(file);

        // Change zip entry's metadata
        try (ZipFile zip = new ZipFile(file.toFile())) {
            final ZipEntry zipEntry = zip.entries().nextElement();
            zipEntry.setComment("some comment");
            zipEntry.setTime(System.currentTimeMillis() + 1000);

            overwriteZipFileContents(file, zipEntry);
        }

        assertEquals(baselineFingerprint, updateFingerprint(file));
        assertNotEquals(originalFileSize, Files.size(file));
    }
    
    @Test
    void zipEntryOrderDoesNotAffectFingerprint() throws IOException {
        final Path zipFile = tempDir.resolve("foo.jar");
        final ZipEntry fooEntry = new ZipEntry("lib/Foo.class");
        final ZipEntry barEntry = new ZipEntry("lib/Bar.class");
        overwriteZipFileContents(zipFile, fooEntry, barEntry);
        final long baselineFingerprint = getBaseLineFingerprint(zipFile);
        
        // swap order
        overwriteZipFileContents(zipFile, barEntry, fooEntry);
        assertEquals(baselineFingerprint, updateFingerprint(zipFile));
    }
    
    @Test
    void nonClassZipEntryDoesNotAffectFingerprint() throws IOException {
        final Path zipFile = tempDir.resolve("foo.jar");
        final ZipEntry fooEntry = new ZipEntry("lib/Foo.class");
        final ZipEntry barEntry = new ZipEntry("bar.properties");
        overwriteZipFileContents(zipFile, fooEntry);
        final long baselineFingerprint = getBaseLineFingerprint(zipFile);
        
        // add a properties file to the jar
        overwriteZipFileContents(zipFile, fooEntry, barEntry);
        assertEquals(baselineFingerprint, updateFingerprint(zipFile));
    }

    @Override
    protected ClasspathEntryFingerprinter newFingerPrinter() {
        return new ZipFileFingerprinter();
    }

    @Override
    protected String[] getValidFileExtensions() {
        return new String[] { "zip", "jar" };
    }

    @Override
    protected String[] getInvalidFileExtensions() {
        return new String[] { "xml" };
    }

    @Override
    protected Path createValidNonEmptyFile() throws IOException {
        final Path zipFile = tempDir.resolve("foo.jar");
        overwriteZipFileContents(zipFile, new ZipEntry("lib/Foo.class"));
        return zipFile;
    }

    private void overwriteZipFileContents(final Path zipFile, final ZipEntry... zipEntries) throws IOException {
        try (ZipOutputStream zipOS = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            for (final ZipEntry zipEntry : zipEntries) {
                zipOS.putNextEntry(zipEntry);
                zipOS.write("content of zip entry".getBytes(StandardCharsets.UTF_8));
                zipOS.closeEntry();
            }
        }
    }

    private long getBaseLineFingerprint(final Path file) throws IOException {
        final Checksum checksum = new Adler32();
        fingerprinter.fingerprint(file, checksum);
        return checksum.getValue();
    }
}
