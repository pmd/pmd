/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.tools.ant.BuildException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.internal.util.IOUtil;

class PMDTaskTest extends AbstractAntTest {

    @BeforeEach
    void setUp() {
        configureProject("src/test/resources/net/sourceforge/pmd/ant/xml/pmdtasktest.xml");
    }

    @Test
    void testFormatterWithNoToFileAttribute() {
        try {
            executeTarget("testFormatterWithNoToFileAttribute");
            fail("This should throw an exception");
        } catch (BuildException ex) {
            assertEquals("toFile or toConsole needs to be specified in Formatter", ex.getMessage());
        }
    }

    @Test
    void testNoRuleSets() {
        try {
            executeTarget("testNoRuleSets");
            fail("This should throw an exception");
        } catch (BuildException ex) {
            assertEquals("No rulesets specified", ex.getMessage());
        }
    }

    @Test
    void testBasic() {
        executeTarget("testBasic");
    }

    @Test
    void testInvalidLanguageVersion() {
        try {
            executeTarget("testInvalidLanguageVersion");
            assertEquals(
                    "The following language is not supported:<sourceLanguage name=\"java\" version=\"42\" />.",
                    log.toString());
            fail("This should throw an exception");
        } catch (BuildException ex) {
            assertEquals(
                    "The following language is not supported:<sourceLanguage name=\"java\" version=\"42\" />.",
                    ex.getMessage());
        }
    }

    @Test
    void testRelativizeWith() throws IOException {
        executeTarget("testRelativizeWith");

        try (InputStream in = Files.newInputStream(Paths.get("target/pmd-ant-test.txt"))) {
            String actual = IOUtil.readToString(in, StandardCharsets.UTF_8);
            // remove any trailing newline
            actual = actual.replaceAll("\n|\r", "");
            assertThat(actual, containsString("src" + File.separator + "sample.dummy:1:\tSampleXPathRule:\tTest Rule 2"));
        }
    }

    @Test
    void testXmlFormatter() throws IOException {
        executeTarget("testXmlFormatter");

        try (InputStream in = Files.newInputStream(Paths.get("target/pmd-ant-xml.xml"));
             InputStream expectedStream = PMDTaskTest.class.getResourceAsStream("xml/expected-pmd-ant-xml.xml")) {
            String actual = IOUtil.readToString(in, StandardCharsets.UTF_8);
            actual = actual.replaceFirst("timestamp=\"[^\"]+\"", "timestamp=\"\"");
            actual = actual.replaceFirst("\\.xsd\" version=\"[^\"]+\"", ".xsd\" version=\"\"");

            String expected = IOUtil.readToString(expectedStream, StandardCharsets.UTF_8);
            expected = expected.replaceFirst("timestamp=\"[^\"]+\"", "timestamp=\"\"");
            expected = expected.replaceFirst("\\.xsd\" version=\"[^\"]+\"", ".xsd\" version=\"\"");

            assertEquals(expected, actual);
        }
    }
}
