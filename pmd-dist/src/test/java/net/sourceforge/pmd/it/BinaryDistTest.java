/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.it;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import net.sourceforge.pmd.PMD;

public class BinaryDistTest {

    @Test
    public void testFileExistence() {
        File file = new File(".", "target/pmd-bin-" + PMD.VERSION + ".zip");
        assertTrue(file.exists());
    }
}
