/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import java.io.File;
import java.nio.file.Path;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;

import net.sourceforge.pmd.PMDVersion;

public abstract class AbstractBinaryDistributionTest {
    public static final String PMD_BIN_PREFIX = "pmd-bin-";

    protected static File getBinaryDistribution() {
        return new File(".", "target/" + PMD_BIN_PREFIX + PMDVersion.VERSION + ".zip");
    }

    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    /**
     * The temporary directory, to which the binary distribution will be extracted.
     * It will be deleted again after the test.
     */
    protected static Path tempDir;

    @BeforeClass
    public static void setupTempDirectory() throws Exception {
        tempDir = folder.newFolder().toPath();
        if (getBinaryDistribution().exists()) {
            ZipFileExtractor.extractZipFile(getBinaryDistribution().toPath(), tempDir);
        }
    }
}
