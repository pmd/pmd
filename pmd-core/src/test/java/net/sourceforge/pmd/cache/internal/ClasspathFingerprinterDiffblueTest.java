package net.sourceforge.pmd.cache.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class ClasspathFingerprinterDiffblueTest {
    /**
     * Method under test: {@link ClasspathFingerprinter#fingerprint(URL[])}
     */
    @Test
    void testFingerprint() throws MalformedURLException {
        // Arrange, Act and Assert
        assertEquals(1L, AbstractAnalysisCache.FINGERPRINTER
                .fingerprint(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri().toURL()));
        assertEquals(1L, AbstractAnalysisCache.FINGERPRINTER
                .fingerprint(Paths.get(System.getProperty("java.io.tmpdir"), "Ignoring classpath entry {}").toUri().toURL()));
    }
}
