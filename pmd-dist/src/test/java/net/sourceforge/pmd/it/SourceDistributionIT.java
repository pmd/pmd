/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import net.sourceforge.pmd.PMDVersion;

public class SourceDistributionIT {
    private File getSourceDistribution() {
        return new File(".", "target/pmd-src-" + PMDVersion.VERSION + ".zip");
    }

    @Test
    public void testFileExistence() {
        assertTrue(getSourceDistribution().exists());
    }
}
