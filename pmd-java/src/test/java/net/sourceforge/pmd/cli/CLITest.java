/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
public class CLITest extends BaseCLITest {
    @Test
    public void minimalArgs() {
        runTest("-d", SOURCE_FOLDER, "-f", "text", "-R", "category/java/bestpractices.xml,category/java/design.xml");
    }

    @Test
    public void minimumPriority() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "category/java/design.xml", "-min", "1", };
        runTest(args);
    }

    @Test
    public void usingDebug() {
        runTest("-d", SOURCE_FOLDER, "-f", "text", "-R", "category/java/design.xml", "-debug");
    }

    @Test
    public void usingDebugLongOption() {
        runTest("-d", SOURCE_FOLDER, "-f", "text", "-R", "category/java/design.xml", "--debug");
    }

    @Test
    public void changeJavaVersion() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "category/java/design.xml", "-version", "1.5", "-language",
                          "java", "--debug", };
        String log = runTest(args);
        Matcher matcher = Pattern.compile("Adding file .*\\.java \\(lang: java 1\\.5\\)").matcher(log);
        assertTrue(matcher.find());
    }

    @Test
    public void exitStatusNoViolations() {
        runTest("-d", SOURCE_FOLDER, "-f", "text", "-R", "category/java/design.xml");
    }

    @Test
    public void exitStatusWithViolations() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "category/java/errorprone.xml", };
        String log = runTest(4, args);
        assertThat(log, containsString("Avoid empty if"));
    }

    @Test
    public void exitStatusWithViolationsAndWithoutFailOnViolations() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "category/java/errorprone.xml", "-failOnViolation", "false", };
        String log = runTest(0, args);
        assertThat(log, containsString("Avoid empty if"));
    }

    @Test
    public void exitStatusWithViolationsAndWithoutFailOnViolationsLongOption() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "category/java/errorprone.xml", "--fail-on-violation", "false", };
        String log = runTest(0, args);
        assertThat(log, containsString("Avoid empty if"));
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     */
    @Test
    public void testWrongRuleset() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "category/java/designn.xml", };
        String log = runTest(1, args);
        assertThat(log, containsString("Can't find resource 'category/java/designn.xml' for rule 'null'."
                                           + "  Make sure the resource is a valid file"));
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     */
    @Test
    public void testWrongRulesetWithRulename() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "category/java/designn.xml/UseCollectionIsEmpty", };
        String log = runTest(1, args);
        assertThat(log, containsString("Can't find resource 'category/java/designn.xml' for rule "
                                           + "'UseCollectionIsEmpty'."));
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     */
    @Test
    public void testWrongRulename() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "category/java/design.xml/ThisRuleDoesNotExist", };
        String log = runTest(1, args);
        assertThat(log, containsString("No rules found. Maybe you misspelled a rule name?"
                                           + " (category/java/design.xml/ThisRuleDoesNotExist)"));
    }
}
