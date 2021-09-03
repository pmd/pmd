/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;

import org.junit.Test;

import net.sourceforge.pmd.cli.BaseCPDCLITest;

public class CPDCommandLineInterfaceTest extends BaseCPDCLITest {
    @Test
    public void shouldFindDuplicatesWithDifferentFileExtensions() {
        runCPD("--minimum-tokens", "5", "--language", "js", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/ts/File1.ts",
                "src/test/resources/net/sourceforge/pmd/cpd/ts/File2.ts");

        assertThat(getOutput(), containsString("Found a 9 line (32 tokens) duplication in the following files"));
    }

    @Test
    public void shouldFindNoDuplicatesWithDifferentFileExtensions() {
        runCPD("--minimum-tokens", "5", "--language", "js", "--files",
                "src/test/resources/net/sourceforge/pmd/cpd/ts/");

        assertThat(getOutput().trim(), emptyString());
    }
}
