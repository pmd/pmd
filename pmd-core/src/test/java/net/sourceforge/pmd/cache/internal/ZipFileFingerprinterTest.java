/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;

class ZipFileFingerprinterTest extends AbstractClasspathEntryFingerprinterTest {

    @Test
    void zipEntryMetadataDoesNotAffectFingerprint() throws IOException {
        final File file = createValidNonEmptyFile();
        final long baselineFingerprint = getBaseLineFingerprint(file);
        final long originalFileSize = file.length();

        // Change zip entry's metadata
        try (ZipFile zip = new ZipFile(file)) {
            final ZipEntry zipEntry = zip.entries().nextElement();
            zipEntry.setComment("some comment");
            zipEntry.setTime(System.currentTimeMillis() + 1000);

            overwriteZipFileContents(file, zipEntry);
        }

        assertEquals(baselineFingerprint, updateFingerprint(file));
        assertNotEquals(originalFileSize, file.length());
    }
    
    @Test
    void zipEntryOrderDoesNotAffectFingerprint() throws IOException {
        final File zipFile = tempDir.resolve("foo.jar").toFile();
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
        final File zipFile = tempDir.resolve("foo.jar").toFile();
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
    protected File createValidNonEmptyFile() throws IOException {
        final File zipFile = tempDir.resolve("foo.jar").toFile();
        overwriteZipFileContents(zipFile, new ZipEntry("lib/Foo.class"));
        return zipFile;
    }

    private void overwriteZipFileContents(final File zipFile, final ZipEntry... zipEntries) throws IOException {
        try (ZipOutputStream zipOS = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
            for (final ZipEntry zipEntry : zipEntries) {
                zipOS.putNextEntry(zipEntry);
                zipOS.write("content of zip entry".getBytes(StandardCharsets.UTF_8));
                zipOS.closeEntry();
            }
        }
    }

    private long getBaseLineFingerprint(final File file) throws MalformedURLException, IOException {
        final Checksum checksum = new Adler32();
        fingerprinter.fingerprint(file.toURI().toURL(), checksum);
        return checksum.getValue();
    }
}
