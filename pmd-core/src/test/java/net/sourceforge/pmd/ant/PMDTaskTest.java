/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import net.sourceforge.pmd.util.IOUtil;

public class PMDTaskTest {

    @Rule
    public final BuildFileRule buildRule = new BuildFileRule();

    @Before
    public void setUp() {
        buildRule.configureProject("src/test/resources/net/sourceforge/pmd/ant/xml/pmdtasktest.xml");
    }

    @Test
    public void testFormatterWithNoToFileAttribute() {
        try {
            buildRule.executeTarget("testFormatterWithNoToFileAttribute");
            fail("This should throw an exception");
        } catch (BuildException ex) {
            Assert.assertEquals("toFile or toConsole needs to be specified in Formatter", ex.getMessage());
        }
    }

    @Test
    public void testNoRuleSets() {
        try {
            buildRule.executeTarget("testNoRuleSets");
            fail("This should throw an exception");
        } catch (BuildException ex) {
            Assert.assertEquals("No rulesets specified", ex.getMessage());
        }
    }

    @Test
    public void testBasic() {
        buildRule.executeTarget("testBasic");
    }

    @Test
    public void testInvalidLanguageVersion() {
        try {
            buildRule.executeTarget("testInvalidLanguageVersion");
            Assert.assertEquals(
                    "The following language is not supported:<sourceLanguage name=\"java\" version=\"42\" />.",
                    buildRule.getLog());
            fail("This should throw an exception");
        } catch (BuildException ex) {
            Assert.assertEquals(
                    "The following language is not supported:<sourceLanguage name=\"java\" version=\"42\" />.",
                    ex.getMessage());
        }
    }

    @Test
    public void testWithShortFilenames() throws IOException {
        buildRule.executeTarget("testWithShortFilenames");

        try (InputStream in = Files.newInputStream(Paths.get("target/pmd-ant-test.txt"))) {
            String actual = IOUtil.readToString(in, StandardCharsets.UTF_8);
            // remove any trailing newline
            actual = actual.replaceAll("\n|\r", "");
            Assert.assertEquals(IOUtil.normalizePath("src/sample.dummy") + ":0:\tSampleXPathRule:\tTest Rule 2", actual);
        }

        assertThat(buildRule.getLog(), containsString("DEPRECATED - Use of shortFilenames is deprecated. Use a nested relativePathsWith element instead."));
    }

    @Test
    public void testRelativizeWith() throws IOException {
        buildRule.executeTarget("testRelativizeWith");

        try (InputStream in = Files.newInputStream(Paths.get("target/pmd-ant-test.txt"))) {
            String actual = IOUtil.readToString(in, StandardCharsets.UTF_8);
            // remove any trailing newline
            actual = actual.replaceAll("\n|\r", "");
            Assert.assertEquals(IOUtil.normalizePath("src/sample.dummy") + ":0:\tSampleXPathRule:\tTest Rule 2", actual);
        }
    }

    @Test
    public void testXmlFormatter() throws IOException {
        buildRule.executeTarget("testXmlFormatter");

        try (InputStream in = new FileInputStream("target/pmd-ant-xml.xml");
             InputStream expectedStream = PMDTaskTest.class.getResourceAsStream("xml/expected-pmd-ant-xml.xml")) {
            String actual = IOUtil.readToString(in, StandardCharsets.UTF_8);
            actual = actual.replaceFirst("timestamp=\"[^\"]+\"", "timestamp=\"\"");
            actual = actual.replaceFirst("\\.xsd\" version=\"[^\"]+\"", ".xsd\" version=\"\"");

            String expected = IOUtil.readToString(expectedStream, StandardCharsets.UTF_8);
            expected = expected.replaceFirst("timestamp=\"[^\"]+\"", "timestamp=\"\"");
            expected = expected.replaceFirst("\\.xsd\" version=\"[^\"]+\"", ".xsd\" version=\"\"");

            // under windows, the file source sample.dummy has different line endings
            // and therefore the endcolumn of the nodes also change
            if (System.lineSeparator().equals("\r\n")) {
                expected = expected.replaceFirst("endcolumn=\"109\"", "endcolumn=\"110\"");
            }

            Assert.assertEquals(expected, actual);
        }
    }
}
