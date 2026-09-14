/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import static net.sourceforge.pmd.reporting.ReportTestUtil.getReport;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import net.sourceforge.pmd.DummyParsingHelper;
import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.xpath.internal.SaxonXPathRuleQuery;

class XPathViolationSuppressorTest {

    private static final String SUPPRESSION_XPATH = ".[@Image = 'Foo']";

    @RegisterExtension
    private final DummyParsingHelper helper = new DummyParsingHelper();

    @Test
    void reusesQueryForSameRuleAndFile() {
        XPathViolationSuppressor suppressor = new XPathViolationSuppressor();
        FooRule rule = ruleWithXPathSuppression();
        DummyRootNode root = helper.parse("(Foo)(Bar)");

        SaxonXPathRuleQuery first = suppressor.getQuery(rule, root.getChild(0), SUPPRESSION_XPATH);
        SaxonXPathRuleQuery second = suppressor.getQuery(rule, root.getChild(1), SUPPRESSION_XPATH);

        assertSame(first, second);
    }

    @Test
    void separatesQueriesByRuleAndFile() {
        XPathViolationSuppressor suppressor = new XPathViolationSuppressor();
        FooRule firstRule = ruleWithXPathSuppression();
        FooRule secondRule = ruleWithXPathSuppression();
        DummyRootNode firstRoot = helper.parse("(Foo)");
        DummyRootNode secondRoot = helper.parse("(Foo)");

        SaxonXPathRuleQuery first = suppressor.getQuery(firstRule, firstRoot.getChild(0), SUPPRESSION_XPATH);
        SaxonXPathRuleQuery otherRule = suppressor.getQuery(secondRule, firstRoot.getChild(0), SUPPRESSION_XPATH);
        SaxonXPathRuleQuery otherFile = suppressor.getQuery(firstRule, secondRoot.getChild(0), SUPPRESSION_XPATH);

        assertNotSame(first, otherRule);
        assertNotSame(first, otherFile);
    }

    @Test
    void preservesSuppressionResults() {
        FooRule rule = ruleWithXPathSuppression();
        Report report = getReport(rule, (r, ctx) -> {
            DummyRootNode root = helper.parse("(Foo)(Bar)(Foo)");
            root.children().forEach(ctx::addViolation);
        });

        assertEquals(1, report.getViolations().size());
        assertEquals(2, report.getSuppressedViolations().size());
    }

    private static FooRule ruleWithXPathSuppression() {
        FooRule rule = new FooRule();
        rule.setProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR, Optional.of(SUPPRESSION_XPATH));
        return rule;
    }
}
