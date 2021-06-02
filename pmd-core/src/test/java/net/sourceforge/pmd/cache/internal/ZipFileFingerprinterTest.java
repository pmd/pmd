
package net.sourceforge.pmd.cache.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.junit.Assert;
import org.junit.Test;

public class ZipFileFingerprinterTest extends AbstractClasspathEntryFingerprinterTest {

    @Test
    public void zipEntryMetadataDoesNotAffectFingerprint() throws IOException {
        final File file = createValidNonEmptyFile();
        final long baselineFingerprint = getBaseLineFingerprint(file);
        final long originalFileSize = file.length();
        
        // Change zip entry's metadata
        try (final ZipFile zip = new ZipFile(file)) {
            final ZipEntry zipEntry = zip.entries().nextElement();
            zipEntry.setComment("some comment");
            zipEntry.setTime(System.currentTimeMillis() + 1000);
            
            overwriteZipFileContents(file, zipEntry);
        }
        
        Assert.assertEquals(baselineFingerprint, updateFingerprint(file));
        Assert.assertNotEquals(originalFileSize, file.length());
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
        final File zipFile = tempFolder.newFile("foo.jar");
        overwriteZipFileContents(zipFile, new ZipEntry("lib/Foo.class"));
        return zipFile;
    }
    
    private void overwriteZipFileContents(final File zipFile, final ZipEntry... zipEntries) throws IOException {
        try (final ZipOutputStream zipOS = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (final ZipEntry zipEntry : zipEntries) {
                zipOS.putNextEntry(zipEntry);
                zipOS.write(("content of zip entry").getBytes(StandardCharsets.UTF_8));
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
