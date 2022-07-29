/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

import net.sourceforge.pmd.PMD.StatusCode;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 */
public class CLITest extends BaseCLITest {

    private static final String RSET_NO_VIOLATION = "rulesets/testing/rset-without-violations.xml";
    private static final String RSET_WITH_VIOLATION = "rulesets/testing/test-rset-1.xml";

    @Test
    public void minimalArgs() {
        runTest("-d", SOURCE_FOLDER, "-f", "text", "-R", RSET_NO_VIOLATION);
    }

    @Test
    public void minimumPriority() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", RSET_WITH_VIOLATION, "-min", "1", };
        runTest(args);
    }

    @Test
    public void usingDebug() {
        runTest("-d", SOURCE_FOLDER, "-f", "text", "-R", RSET_NO_VIOLATION, "-debug");
    }

    @Test
    public void usingDebugLongOption() {
        runTest("-d", SOURCE_FOLDER, "-f", "text", "-R", RSET_NO_VIOLATION, "--debug");
    }

    @Test
    public void changeJavaVersion() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", RSET_NO_VIOLATION, "-version", "1.5", "-language", "java", "--debug" };
        String log = runTest(args);
        assertThat(log, containsPattern("Adding file .*\\.java \\(lang: java 1\\.5\\)"));
    }

    @Test
    public void exitStatusNoViolations() {
        runTest("-d", SOURCE_FOLDER, "-f", "text", "-R", "rulesets/testing/rset-without-violations.xml");
    }

    @Test
    public void exitStatusWithViolations() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", RSET_WITH_VIOLATION, };
        String log = runTest(StatusCode.VIOLATIONS_FOUND, args);
        assertThat(log, containsString("Violation from test-rset-1.xml"));
    }

    @Test
    public void exitStatusWithViolationsAndWithoutFailOnViolations() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", RSET_WITH_VIOLATION, "-failOnViolation", "false", };
        String log = runTest(StatusCode.OK, args);
        assertThat(log, containsString("Violation from test-rset-1.xml"));
    }

    @Test
    public void exitStatusWithViolationsAndWithoutFailOnViolationsLongOption() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", RSET_WITH_VIOLATION, "--fail-on-violation", "false", };
        String log = runTest(StatusCode.OK, args);
        assertThat(log, containsString("Violation from test-rset-1.xml"));
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     */
    @Test
    public void testWrongRuleset() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "category/java/designn.xml", };
        String log = runTest(StatusCode.ERROR, args);
        assertThat(log, containsString("Cannot resolve rule/ruleset reference "
                                       + "'category/java/designn.xml'"));
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     */
    @Test
    public void testWrongRulesetWithRulename() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "category/java/designn.xml/UseCollectionIsEmpty", };
        String log = runTest(StatusCode.ERROR, args);
        assertThat(log, containsString("Cannot resolve rule/ruleset reference"
                                       + " 'category/java/designn.xml/UseCollectionIsEmpty'"));
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1231/
     */
    @Test
    public void testWrongRulename() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", RSET_WITH_VIOLATION + "/ThisRuleDoesNotExist", };
        String log = runTest(StatusCode.OK, args);
        assertThat(log, containsString("No rules found. Maybe you misspelled a rule name?"
                                       + " (" + RSET_WITH_VIOLATION + "/ThisRuleDoesNotExist)"));
    }
}
