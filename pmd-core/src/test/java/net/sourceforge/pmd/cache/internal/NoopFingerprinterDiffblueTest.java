package net.sourceforge.pmd.cache.internal;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.zip.Checksum;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class NoopFingerprinterDiffblueTest {
    /**
     * Method under test: {@link NoopFingerprinter#appliesTo(String)}
     */
    @Test
    void testAppliesTo() {
        // Arrange, Act and Assert
        assertTrue((new NoopFingerprinter()).appliesTo("File Extension"));
    }

    /**
     * Method under test: {@link NoopFingerprinter#fingerprint(URL, Checksum)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testFingerprint() throws IOException {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        NoopFingerprinter noopFingerprinter = null;
        URL entry = null;
        Checksum checksum = null;

        // Act
        noopFingerprinter.fingerprint(entry, checksum);

        // Assert
        // TODO: Add assertions on result
    }
}
