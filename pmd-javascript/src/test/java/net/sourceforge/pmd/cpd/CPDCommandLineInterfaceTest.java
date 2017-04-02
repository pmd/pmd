/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.cli.BaseCPDCLITest;

public class CPDCommandLineInterfaceTest extends BaseCPDCLITest {
    @Test
    public void shouldFindDuplicatesWithDifferentFileExtensions() {
        runCPD("--minimum-tokens", "5", "--language", "js", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/ts/File1.ts",
                "src/test/resources/net/sourceforge/pmd/cpd/ts/File2.ts");

        String out = getOutput();
        Assert.assertTrue(out.contains("Found a 9 line (30 tokens) duplication in the following files"));
    }

    @Test
    public void shouldFindNoDuplicatesWithDifferentFileExtensions() {
        runCPD("--minimum-tokens", "5", "--language", "js", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/ts/");

        String out = getOutput();
        Assert.assertTrue(out.trim().isEmpty());
    }
}
