/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.cli.BaseCLITest;

public class XmlCliTest extends BaseCLITest {
    private static final String BASE_DIR = "src/test/resources/net/sourceforge/pmd/lang/xml/cli-tests/sampleproject";
    private static final String RULE_MESSAGE = "A tags are not allowed";

    private String[] createArgs(String directory, String ... args) {
        List<String> arguments = new ArrayList<>();
        arguments.add("-f");
        arguments.add("text");
        arguments.add("-no-cache");
        arguments.add("-R");
        arguments.add(BASE_DIR + "/ruleset.xml");
        arguments.add("-d");
        arguments.add(BASE_DIR + directory);
        arguments.addAll(Arrays.asList(args));
        return arguments.toArray(new String[0]);
    }

    @Test
    public void analyzeSingleXmlWithoutForceLanguage() {
        String resultFilename = runTest(createArgs("/src/file1.ext"), "analyzeSingleXmlWithoutForceLanguage", 0);
        assertRuleMessage(0, resultFilename);
    }

    @Test
    public void analyzeSingleXmlWithForceLanguage() {
        String resultFilename = runTest(createArgs("/src/file1.ext", "-force-language", "xml"),
            "analyzeSingleXmlWithForceLanguage", 4);
        assertRuleMessage(1, resultFilename);
    }

    @Test
    public void analyzeDirectoryWithForceLanguage() {
        String resultFilename = runTest(createArgs("/src/", "-force-language", "xml"),
            "analyzeDirectoryWithForceLanguage", 4);
        assertRuleMessage(3, resultFilename);
    }

    private void assertRuleMessage(int expectedCount, String resultFilename) {
        try {
            String result = FileUtils.readFileToString(new File(resultFilename), StandardCharsets.UTF_8);
            Assert.assertEquals(expectedCount, StringUtils.countMatches(result, RULE_MESSAGE));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
