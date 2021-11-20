/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetLoader;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.testframework.PmdRuleTst;

/**
 * Test class to test if empty if statements are captured
 * and no empty block statement error is thrown
 *
 */
public class EmptyIfStmtTest extends PmdRuleTst {
    /**
     * Assert error string, in case of failures
     */
    private static final String MATCH_ERROR = "Expected Violation count do not match";
    /**
     * Rule set factory initialization
     */
    private static RuleSetFactory factory = new RuleSetLoader().enableCompatibility(false).toFactory();
    /**
     * language variable to hold apex language configuration
     */
    private static final LanguageVersion LANGUAGE_VERSION = LanguageRegistry
                                                                .getLanguage(ApexLanguageModule.NAME)
                                                                .getDefaultVersion();
    /**
     * boolean to control unit test run class path
     */
    private static final boolean USE_CLASS_PATH = true;
    /**
     * variable to hold ruleset
     */
    private static RuleSet ruleSet;
    /**
     * variable to hold report of violations
     */
    private static Report rpt;
    /**
     * variable to hold error logs
     */
    private static String errorLog;
    /**empty if block created
     * for testing if block error scenario
     */
    private static final String TESTIF = "public class BlockWithEmptyIf {" + PMD.EOL + "public void methodEmpty() {"
            + PMD.EOL + "int i = 0;" + PMD.EOL
            + PMD.EOL + "if (i<5) {" + PMD.EOL + "}" + PMD.EOL + "}" + PMD.EOL + "}";
    /**non-empty if block created
     * for testing if block no error scenario
     */
    private static final String TESTIFWITHDATA = "public class BlockWithNotEmptyIf {" + PMD.EOL + "public void methodNotEmpty() {"
            + PMD.EOL + "int i = 0;" + PMD.EOL
            + PMD.EOL + "if (i<5) {" + PMD.EOL
            + PMD.EOL + "i++;" + PMD.EOL
            + "}" + PMD.EOL + "}" + PMD.EOL + "}";
    /**empty method block created
     * for testing Block error scenario
     */
    private static final String TESTBLOCK = "public class BlockEmpty {" + PMD.EOL + "public void methodEmpty() {"
            + PMD.EOL + "}" + PMD.EOL + "}";
    /**non-empty method block created
     * for testing no block error scenario
     */
    private static final String TESTBLOCKWITHDATA = "public class BlockNotEmpty {" + PMD.EOL + "public void methodNotEmpty() {"
            + PMD.EOL + "int i = 0;" + PMD.EOL
            + PMD.EOL + "}" + PMD.EOL + "}";

    /**
     * method to initialize ruleset
     * and report before every new test method execution
     **/
    @Before
    public void initialize() {
        initializeRuleSet();
        rpt = new Report();
    }

    private static void initializeRuleSet() {
        try {
            ruleSet = factory.createRuleSet("rulesets/apex/empty.xml");
        } catch (Exception exception) {
            errorLog = errorLog + exception.getMessage();
        }
    }

    /**
     * Test method to test if error is reported if the
     * incoming "if block" has no statements
     **/
    @Test
    public void testEmptyRuleSetIfEmpty() {
        runTestFromString(TESTIF, ruleSet, rpt,
                LANGUAGE_VERSION, USE_CLASS_PATH);
        assertEquals(MATCH_ERROR, 1, rpt.size());
    }

    /**
     * Test method to test if no error is reported
     * if the incoming "if block" has statements
     **/
    @Test
    public void testEmptyRuleSetIfWithData() {
        runTestFromString(TESTIFWITHDATA, ruleSet, rpt,
                LANGUAGE_VERSION, USE_CLASS_PATH);
        assertEquals(MATCH_ERROR, 0, rpt.size());
    }

    /**
     * Test method to test if error is reported
     * if the incoming block has no statements
     * and no if or while or catch block
     **/
    @Test
    public void testEmptyRuleSetBlockEmpty() {
        runTestFromString(TESTBLOCK, ruleSet, rpt,
                LANGUAGE_VERSION, USE_CLASS_PATH);
        assertEquals(MATCH_ERROR, 1, rpt.size());
    }

    /**
    * Test method to test if no errors are reported
     * if the incoming block has statements
     **/
    @Test
    public void testEmptyRuleSetBlockNotEmpty() {
        runTestFromString(TESTBLOCKWITHDATA, ruleSet, rpt,
                LANGUAGE_VERSION, USE_CLASS_PATH);
        assertEquals(MATCH_ERROR, 0, rpt.size());
    }
}
