/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.util.FileUtil;

/**
 * @author Romain Pelisse <belaran@gmail.com>
 * 
 */
public class CLITest extends BaseCLITest {
    @Test
    public void minimalArgs() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "java-unnecessary,java-design", };
        runTest(args, "minimalArgs");
    }

    @Test
    public void minimumPriority() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "java-design", "-min", "1", };
        runTest(args, "minimumPriority");
    }

    @Test
    public void usingDebug() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "java-design", "-debug", };
        runTest(args, "minimalArgsWithDebug");
    }

    @Test
    public void changeJavaVersion() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "java-design", "-version", "1.5", "-language",
            "java", "-debug", };
        String resultFilename = runTest(args, "chgJavaVersion");
        assertTrue("Invalid Java version",
                FileUtil.findPatternInFile(new File(resultFilename), "Using Java version: Java 1.5"));
    }

    @Test
    public void exitStatusNoViolations() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "java-design", };
        runTest(args, "exitStatusNoViolations");
    }

    @Test
    public void exitStatusWithViolations() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "java-empty", };
        String resultFilename = runTest(args, "exitStatusWithViolations", 4);
        assertTrue(FileUtil.findPatternInFile(new File(resultFilename), "Avoid empty if"));
    }

    @Test
    public void exitStatusWithViolationsAndWithoutFailOnViolations() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "java-empty", "-failOnViolation", "false", };
        String resultFilename = runTest(args, "exitStatusWithViolationsAndWithoutFailOnViolations", 0);
        assertTrue(FileUtil.findPatternInFile(new File(resultFilename), "Avoid empty if"));
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     */
    @Test
    public void testWrongRuleset() throws Exception {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "java-designn", };
        String filename = TEST_OUPUT_DIRECTORY + "testWrongRuleset.txt";
        createTestOutputFile(filename);
        runPMDWith(args);
        Assert.assertEquals(1, getStatusCode());
        assertTrue(FileUtil.findPatternInFile(new File(filename),
                "Can't find resource 'null' for rule 'java-designn'." + "  Make sure the resource is a valid file"));
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     */
    @Test
    public void testWrongRulesetWithRulename() throws Exception {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "java-designn/UseCollectionIsEmpty", };
        String filename = TEST_OUPUT_DIRECTORY + "testWrongRuleset.txt";
        createTestOutputFile(filename);
        runPMDWith(args);
        Assert.assertEquals(1, getStatusCode());
        assertTrue(FileUtil.findPatternInFile(new File(filename),
                "Can't find resource 'null' for rule " + "'java-designn/UseCollectionIsEmpty'."));
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     */
    @Test
    public void testWrongRulename() throws Exception {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "java-design/ThisRuleDoesNotExist", };
        String filename = TEST_OUPUT_DIRECTORY + "testWrongRuleset.txt";
        createTestOutputFile(filename);
        runPMDWith(args);
        Assert.assertEquals(1, getStatusCode());
        assertTrue(FileUtil.findPatternInFile(new File(filename), Pattern
                .quote("No rules found. Maybe you mispelled a rule name?" + " (java-design/ThisRuleDoesNotExist)")));
    }
}
