package net.sourceforge.pmd.cache.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.zip.Checksum;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ZipFileFingerprinterDiffblueTest {
    /**
     * Method under test: {@link ZipFileFingerprinter#appliesTo(String)}
     */
    @Test
    void testAppliesTo() {
        // Arrange, Act and Assert
        assertFalse((new ZipFileFingerprinter()).appliesTo("File Extension"));
        assertTrue((new ZipFileFingerprinter()).appliesTo("jar"));
    }

    /**
     * Method under test: {@link ZipFileFingerprinter#fingerprint(URL, Checksum)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testFingerprint() throws IOException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        ZipFileFingerprinter zipFileFingerprinter = null;
        URL entry = null;
        Checksum checksum = null;

        // Act
        zipFileFingerprinter.fingerprint(entry, checksum);

        // Assert
        // TODO: Add assertions on result
    }
}
