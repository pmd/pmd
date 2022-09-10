/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.PMD.StatusCode;
import net.sourceforge.pmd.cli.BaseCLITest;

public class XmlCliTest extends BaseCLITest {
    private static final String BASE_DIR = "src/test/resources/net/sourceforge/pmd/lang/xml/cli-tests/sampleproject";
    private static final String RULE_MESSAGE = "A tags are not allowed";

    private String[] createArgs(String directory, String... args) {
        List<String> arguments = new ArrayList<>(listOf(
            "-f",
            "text",
            "--no-cache",
            "--no-progress",
            "-R",
            BASE_DIR + "/ruleset.xml",
            "-d",
            BASE_DIR + directory
        ));
        arguments.addAll(Arrays.asList(args));
        return arguments.toArray(new String[0]);
    }

    @Test
    public void analyzeSingleXmlWithoutForceLanguage() {
        String log = runTest(StatusCode.OK, createArgs("/src/file1.ext"));
        assertRuleMessage(0, log);
    }

    @Test
    public void analyzeSingleXmlWithForceLanguage() {
        String log = runTest(StatusCode.VIOLATIONS_FOUND, createArgs("/src/file1.ext", "-force-language", "xml"));
        assertRuleMessage(1, log);
    }

    @Test
    public void analyzeDirectoryWithForceLanguage() {
        String log = runTest(StatusCode.VIOLATIONS_FOUND, createArgs("/src/", "-force-language", "xml"));
        assertRuleMessage(3, log);
    }

    private void assertRuleMessage(int expectedCount, String log) {
        Assert.assertEquals(expectedCount, StringUtils.countMatches(log, RULE_MESSAGE));
    }
}
