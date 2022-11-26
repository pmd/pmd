/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.PMDVersion;

abstract class AbstractBinaryDistributionTest {
    public static final String PMD_BIN_PREFIX = "pmd-bin-";

    protected static File getBinaryDistribution() {
        return new File(".", "target/" + PMD_BIN_PREFIX + PMDVersion.VERSION + ".zip");
    }

    @TempDir
    static Path folder;

    /**
     * The temporary directory, to which the binary distribution will be extracted.
     * It will be deleted again after the test.
     */
    protected static Path tempDir;

    protected Path createTemporaryReportFile() throws IOException {
        return Files.createTempFile(folder, null, null);
    }

    @BeforeAll
    static void setupTempDirectory() throws Exception {
        tempDir = Files.createTempDirectory(folder, null);
        if (getBinaryDistribution().exists()) {
            ZipFileExtractor.extractZipFile(getBinaryDistribution().toPath(), tempDir);
        }
    }
}
