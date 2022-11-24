/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDVersion;

class SourceDistributionIT {
    private static final String BASE_PATH = "pmd-src-" + PMDVersion.VERSION;

    private File getSourceDistribution() {
        return new File(".", "target/" + BASE_PATH + ".zip");
    }

    @Test
    void testFileExistence() {
        assertTrue(getSourceDistribution().exists());
    }

    @Test
    void verifyExclusions() throws Exception {
        Set<String> exclusions = new HashSet<>();
        exclusions.add(BASE_PATH + "/.ci/files/id_rsa");
        exclusions.add(BASE_PATH + "/.ci/files/private-env");
        exclusions.add(BASE_PATH + "/.ci/files/public-env");
        exclusions.add(BASE_PATH + "/.ci/files/release-signing-key-D0BF1D737C9A1C22.gpg.gpg");
        List<String> files = ZipFileExtractor.readZipFile(getSourceDistribution().toPath());

        for (String file : files) {
            assertFalse(exclusions.contains(file), "File " + file + " must not be included");
        }
    }
}
