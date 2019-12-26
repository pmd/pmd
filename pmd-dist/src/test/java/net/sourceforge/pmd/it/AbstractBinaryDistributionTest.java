/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import net.sourceforge.pmd.PMDVersion;

public abstract class AbstractBinaryDistributionTest {

    protected static File getBinaryDistribution() {
        return new File(".", "target/pmd-bin-" + PMDVersion.VERSION + ".zip");
    }

    /**
     * The temporary directory, to which the binary distribution will be extracted.
     * It will be deleted again after the test.
     */
    protected static Path tempDir;

    @BeforeClass
    public static void setupTempDirectory() throws Exception {
        tempDir = Files.createTempDirectory("pmd-it-test-");
        if (getBinaryDistribution().exists()) {
            ZipFileExtractor.extractZipFile(getBinaryDistribution().toPath(), tempDir);
        }
    }

    @AfterClass
    public static void cleanupTempDirectory() throws IOException {
        if (tempDir != null && tempDir.toFile().exists()) {
            FileUtils.forceDelete(tempDir.toFile());
        }
    }
}
