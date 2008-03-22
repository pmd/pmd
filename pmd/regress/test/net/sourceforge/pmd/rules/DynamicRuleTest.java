/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.util.ResourceLoader;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

@RunWith(Parameterized.class)
public class DynamicRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    private String strRuleset;

    public DynamicRuleTest(String strRuleset, Rule rule) {
        this.rule = rule;
        this.strRuleset = strRuleset;
    }

    @Parameters
    public static Collection data() throws IOException, RuleSetNotFoundException {
        List<Object[]> allRules = new ArrayList<Object[]>();
        RuleSetFactory rsf = new RuleSetFactory();
        String rulesetFilenames = null;
        Properties props = new Properties();
        props.load(ResourceLoader.loadResourceAsStream("rulesets/rulesets.properties"));
        rulesetFilenames = props.getProperty("rulesets.testnames");
        StringTokenizer st = new StringTokenizer(rulesetFilenames, ",");
        while (st.hasMoreTokens()) {
            String strRule = st.nextToken();
            String strCleanRule = cleanRulesetName(strRule);
            RuleSets ruleSets = rsf.createRuleSets(strRule);
            for (Iterator<RuleSet> iter = ruleSets.getRuleSetsIterator(); iter.hasNext();) {
                RuleSet ruleSet = iter.next();
                for (Rule rule : ruleSet.getRules()) {
                    allRules.add(new Object[] { strCleanRule, rule });
                }
            }
        }

        return allRules;
    }

    private static String cleanRulesetName(String strRule) {
        return strRule.substring(strRule.indexOf('/') + 1, strRule.indexOf('.')).replaceAll("-", "") + "/xml/";
    }

    @Ignore
    @Test
    public void testAll() {
        TestDescriptor[] td = extractTestsFromXml(rule, getCleanRuleName(rule), strRuleset);
        runTests(td);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(DynamicRuleTest.class);
    }
}
