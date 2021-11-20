/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetLoader;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.testframework.PmdRuleTst;


public class EmptyIfStmtTest extends PmdRuleTst {

    private RuleSetFactory factory = new RuleSetLoader().enableCompatibility(false).toFactory();

    @Test
    public void testEmptyRuleSetIfEmpty() throws Exception {
        RuleSet ruleSet = factory.createRuleSet("rulesets/apex/empty.xml");
        Report rpt = new Report();
        runTestFromString(TESTIF, ruleSet, rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion(), true);
        assertEquals(1, rpt.size());
    }

    @Test
    public void testEmptyRuleSetBlockEmpty() throws Exception {
        RuleSet ruleSet = factory.createRuleSet("rulesets/apex/empty.xml");
        Report rpt = new Report();
        runTestFromString(TESTBLOCK, ruleSet, rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion(), true);
        assertEquals(1, rpt.size());
    }

    private static final String TESTIF = "public class BlockWithEmptyIf {" + PMD.EOL + "public void methodEmpty() {"
             + PMD.EOL + "int i = 0;" + PMD.EOL
             + PMD.EOL + "if (i<5) {" + PMD.EOL + "}" + PMD.EOL + "}" + PMD.EOL + "}";

    private static final String TESTBLOCK = "public class BlockEmpty {" + PMD.EOL + "public void methodEmpty() {"
            + PMD.EOL + "}" + PMD.EOL + "}";
}
