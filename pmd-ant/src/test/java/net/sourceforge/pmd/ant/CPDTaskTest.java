/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tools.ant.BuildException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.internal.util.IOUtil;

/**
 *
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
class CPDTaskTest extends AbstractAntTest {

    @BeforeEach
    void setUp() {
        configureProject("src/test/resources/net/sourceforge/pmd/ant/xml/cpdtasktest.xml");
    }

    @Test
    void testBasic() throws IOException {
        executeTarget("testBasic");
        assertReport("target/cpd.ant.tests");
    }

    @Test
    void failOnErrorDefault() throws IOException {
        BuildException buildException = assertThrows(BuildException.class, () -> executeTarget("failOnErrorDefault"));
        assertThat(buildException.getMessage(), containsString("There were 1 recovered errors during analysis."));
        assertReport("target/cpd.ant.tests");
    }

    @Test
    void failOnErrorIgnore() throws IOException {
        executeTarget("failOnErrorIgnore");
        assertReport("target/cpd.ant.tests");
        assertThat(log.toString(), containsString("There were 1 recovered errors during analysis."));
    }

    private static void assertReport(String path) throws IOException {
        Path report = Paths.get(path);
        assertTrue(Files.exists(report), "Report was not created");
        String reportContent = IOUtil.readFileToString(report.toFile(), StandardCharsets.UTF_8);
        assertThat(reportContent, containsString("Found a 1 line (21 tokens) duplication in the following files:"));
        assertThat(reportContent, containsString("sample.dummy"));
        assertThat(reportContent, containsString("sample2.dummy"));
    }
}
