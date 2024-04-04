/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        Path report = Paths.get("target/cpd.ant.tests");
        assertTrue(Files.exists(report), "Report was not created");
        String reportContent = IOUtil.readFileToString(report.toFile(), StandardCharsets.UTF_8);
        assertThat(reportContent, containsString("Found a 1 line (21 tokens) duplication in the following files:"));
        assertThat(reportContent, containsString("sample.dummy"));
        assertThat(reportContent, containsString("sample2.dummy"));
    }
}
