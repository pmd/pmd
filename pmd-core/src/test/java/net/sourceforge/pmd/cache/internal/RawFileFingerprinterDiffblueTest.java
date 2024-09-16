package net.sourceforge.pmd.cache.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

import org.junit.jupiter.api.Test;

class RawFileFingerprinterDiffblueTest {
    /**
     * Method under test: {@link RawFileFingerprinter#appliesTo(String)}
     */
    @Test
    void testAppliesTo() {
        // Arrange, Act and Assert
        assertFalse((new RawFileFingerprinter()).appliesTo("File Extension"));
        assertTrue((new RawFileFingerprinter()).appliesTo("class"));
    }

    /**
     * Method under test: {@link RawFileFingerprinter#fingerprint(URL, Checksum)}
     */
    @Test
    void testFingerprint() throws IOException {
        // Arrange
        RawFileFingerprinter rawFileFingerprinter = new RawFileFingerprinter();
        URL entry = Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri().toURL();
        Adler32 checksum = new Adler32();

        // Act
        rawFileFingerprinter.fingerprint(entry, checksum);

        // Assert
        assertEquals(1L, checksum.getValue());
    }

    /**
     * Method under test: {@link RawFileFingerprinter#fingerprint(URL, Checksum)}
     */
    @Test
    void testFingerprint2() throws IOException {
        // Arrange
        RawFileFingerprinter rawFileFingerprinter = new RawFileFingerprinter();
        URL entry = Paths.get(System.getProperty("java.io.tmpdir"), "foo").toUri().toURL();
        Adler32 checksum = new Adler32();

        // Act
        rawFileFingerprinter.fingerprint(entry, checksum);

        // Assert that nothing has changed
        assertEquals(17629297L, checksum.getValue());
    }
}
