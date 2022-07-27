/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.jaxen;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

public class RegexpAcceptanceTest {

    private static final String XPATH = "//ClassOrInterfaceDeclaration[matches(@Image, 'F?o')]";

    private Rule rule;

    @Before
    public void setUp() {
        rule = new XPathRule(XPathVersion.XPATH_1_0, XPATH);
        rule.setLanguage(LanguageRegistry.getLanguage(JavaLanguageModule.NAME));
        rule.setMessage("F?o matched");
    }

    @Test
    public void shouldMatchFoo() {
        List<RuleViolation> violations = eval("public class Foo {}");
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldNotMatchBar() {
        List<RuleViolation> violations = eval("public class Bar {}");
        assertEquals(0, violations.size());
    }

    @Test
    public void shouldMatchFlo() {
        List<RuleViolation> violations = eval("public class Flo {}");
        assertEquals(1, violations.size());
    }

    private List<RuleViolation> eval(String code) {
        PMDConfiguration config = new PMDConfiguration();
        config.setIgnoreIncrementalAnalysis(true);
        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            pmd.addRuleSet(RuleSet.forSingleRule(rule));
            pmd.files().addSourceFile(code, "RegexpAcceptanceTest.java");

            Report report = pmd.performAnalysisAndCollectReport();
            return report.getViolations();
        }
    }
}
