
package net.sourceforge.pmd.cache.internal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public abstract class AbstractClasspathEntryFingerprinterTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    protected ClasspathEntryFingerprinter fingerprinter = newFingerPrinter();
    protected Checksum checksum = new Adler32();

    @Before
    public void setUp() {
        checksum.reset();
    }

    protected abstract ClasspathEntryFingerprinter newFingerPrinter();
    
    protected abstract String[] getValidFileExtensions();
    protected abstract String[] getInvalidFileExtensions();
    
    protected abstract File createValidNonEmptyFile() throws IOException;
    
    @Test
    public void appliesToNullIsSafe() {
        fingerprinter.appliesTo(null);
    }

    @Parameters(method = "getValidFileExtensions")
    @Test
    public void appliesToValidFile(final String extension) {
        Assert.assertTrue(fingerprinter.appliesTo(extension));
    }

    @Parameters(method = "getInvalidFileExtensions")
    @Test
    public void doesNotApplyToInvalidFile(final String extension) {
        Assert.assertFalse(fingerprinter.appliesTo(extension));
    }

    @Test
    public void fingerprintNonExistingFile() throws MalformedURLException, IOException {
        final long prevValue = checksum.getValue();
        
        fingerprinter.fingerprint(new File("non-existing").toURI().toURL(), checksum);
        
        Assert.assertEquals(prevValue, checksum.getValue());
    }

    @Test
    public void fingerprintExistingValidFile() throws IOException {
        final long prevValue = checksum.getValue();
        final File file = createValidNonEmptyFile();
        
        Assert.assertNotEquals(prevValue, updateFingerprint(file));
    }
    
    protected long updateFingerprint(final File file) throws MalformedURLException, IOException {
        fingerprinter.fingerprint(file.toURI().toURL(), checksum);
        return checksum.getValue();
    }
}
