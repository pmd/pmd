/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractClasspathEntryFingerprinterTest {

    @TempDir
    Path tempDir;

    protected ClasspathEntryFingerprinter fingerprinter = newFingerPrinter();
    protected Checksum checksum = new Adler32();

    @BeforeEach
    void setUp() {
        checksum.reset();
    }

    protected abstract ClasspathEntryFingerprinter newFingerPrinter();

    protected abstract String[] getValidFileExtensions();

    protected abstract String[] getInvalidFileExtensions();

    protected abstract File createValidNonEmptyFile() throws IOException;

    @Test
    void appliesToNullIsSafe() {
        fingerprinter.appliesTo(null);
    }

    @ParameterizedTest
    @MethodSource("getValidFileExtensions")
    void appliesToValidFile(final String extension) {
        assertTrue(fingerprinter.appliesTo(extension));
    }

    @ParameterizedTest
    @MethodSource("getInvalidFileExtensions")
    void doesNotApplyToInvalidFile(final String extension) {
        assertFalse(fingerprinter.appliesTo(extension));
    }

    @Test
    void fingerprintNonExistingFile() throws MalformedURLException, IOException {
        final long prevValue = checksum.getValue();

        fingerprinter.fingerprint(new File("non-existing").toURI().toURL(), checksum);

        assertEquals(prevValue, checksum.getValue());
    }

    @Test
    void fingerprintExistingValidFile() throws IOException {
        final long prevValue = checksum.getValue();
        final File file = createValidNonEmptyFile();

        assertNotEquals(prevValue, updateFingerprint(file));
    }

    protected long updateFingerprint(final File file) throws MalformedURLException, IOException {
        fingerprinter.fingerprint(file.toURI().toURL(), checksum);
        return checksum.getValue();
    }
}
