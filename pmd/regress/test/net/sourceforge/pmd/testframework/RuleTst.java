/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.testframework;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SimpleRuleSetNameMapper;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.SourceTypeToRuleLanguageMapper;
import net.sourceforge.pmd.TargetJDKVersion;

import java.io.StringReader;

public class RuleTst extends TestCase {
    public static final SourceType DEFAULT_SOURCE_TYPE = SourceType.JAVA_14;

    public void runTestFromString(String code, int expectedResults, Rule rule) throws Throwable {
        runTestFromString(code, expectedResults, rule, DEFAULT_SOURCE_TYPE);
    }

    public Rule findRule(String rs, String r) {
        try {
            Rule rule = new RuleSetFactory().createRuleSets(new SimpleRuleSetNameMapper(rs).getRuleSets()).getRuleByName(r);
            if (rule == null) {
                fail("Rule " + r + " not found in ruleset " + rs);
            }
            return rule;
        } catch (RuleSetNotFoundException e) {
            e.printStackTrace();
            fail("Couldn't find ruleset " + rs);
            return null;
        }
    }


    /**
     * Run the rule on the given code, and check the expected number of violations.
     *
     * @param code
     * @param expectedResults
     * @param rule
     * @throws Throwable
     */
    public void runTestFromString(String code, int expectedResults, Rule rule,
                                  SourceType sourceType) throws Throwable {
        int res = processUsingStringReader(code, rule, sourceType).size();
        assertEquals("Expected " + expectedResults + " failures, got " + res + ".",
                expectedResults, res);
    }

    private Report processUsingStringReader(String code, Rule rule,
                                            SourceType sourceType) throws Throwable {
        Report report = new Report();
        runTestFromString(code, rule, report, sourceType);
        return report;
    }

    /**
     * Run the rule on the given code, and put the violations in the given report.
     *
     * @param code
     * @param expectedResults
     * @param rule
     * @throws Throwable
     */
    public void runTestFromString(String code, Rule rule, Report report,
                                  SourceType sourceType) throws Throwable {
        PMD p = new PMD();
        RuleContext ctx = new RuleContext();
        ctx.setReport(report);
        ctx.setSourceCodeFilename("n/a");
        RuleSet rules = new RuleSet();
        rules.addRule(rule);
        rules.setLanguage(SourceTypeToRuleLanguageMapper.getMappedLanguage(sourceType));
        p.processFile(new StringReader(code), new RuleSets(rules), ctx, sourceType);
    }


    public void runTestFromString(String code, Rule rule, Report report) throws Throwable {
        runTestFromString(code, rule, report, DEFAULT_SOURCE_TYPE);
    }

    public void runTestFromString15(String code, Rule rule, Report report) throws Throwable {
        runTestFromString(code, rule, report, SourceType.JAVA_15);
    }

    public void runTestFromString13(String code, Rule rule, Report report) throws Throwable {
        runTestFromString(code, rule, report, SourceType.JAVA_13);
    }

    public void runTestFromString(String code, Rule rule, Report report, TargetJDKVersion jdk)
            throws Throwable {
        PMD p = new PMD(jdk);
        RuleContext ctx = new RuleContext();
        ctx.setReport(report);
        ctx.setSourceCodeFilename("n/a");
        RuleSet rules = new RuleSet();
        rules.addRule(rule);
        p.processFile(new StringReader(code), rules, ctx);
    }
}
