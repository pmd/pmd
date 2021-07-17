/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

public class XMLVerboseRendererTest extends XMLRendererTest {

    @Override
    public Renderer getRenderer() {
        return new XMLVerboseRenderer();
    }

    protected String getExampleCodeSnippet() {
        return "private final String snippet;";
    }

    @Override
    public String getExpected() {
        return getHeader() + "<file name=\"" + getSourceCodeFilename() + "\">" + PMD.EOL
            + "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"1\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">"
            + PMD.EOL + "blah" + PMD.EOL + "</violation>" + PMD.EOL + "<codesnippet>" + PMD.EOL + getExampleCodeSnippet()
            + PMD.EOL + "</codesnippet>" + PMD.EOL + "</file>" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return getHeader() + "</pmd>" + PMD.EOL;
    }

    @Override
    public String getExpectedMultiple() {
        return getHeader() + "<file name=\"" + getSourceCodeFilename() + "\">" + PMD.EOL
            + "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"1\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">"
            + PMD.EOL + "blah" + PMD.EOL + "</violation>" + PMD.EOL
            + "<codesnippet>" + PMD.EOL + getExampleCodeSnippet() + PMD.EOL + "</codesnippet>" + PMD.EOL
            + "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"2\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"1\">"
            + PMD.EOL + "blah" + PMD.EOL + "</violation>" + PMD.EOL
            + "<codesnippet>" + PMD.EOL + getExampleCodeSnippet() + PMD.EOL + "</codesnippet>" + PMD.EOL
            + "</file>" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    protected RuleViolation newRuleViolation(int column) {
        RuleViolation violation = super.newRuleViolation(column);
        if (violation instanceof ParametricRuleViolation<?>) {
            ((ParametricRuleViolation<?>) violation).setRawCodeSnippet(getExampleCodeSnippet());
        }
        return violation;
    }

    @Override
    protected RuleViolation newRuleViolation(Rule theRule, int endColumn) {
        RuleViolation violation = super.newRuleViolation(theRule, endColumn);
        if (violation instanceof ParametricRuleViolation<?>) {
            ((ParametricRuleViolation<?>) violation).setRawCodeSnippet(getExampleCodeSnippet());
        }
        return violation;
    }

    @Override
    protected RuleViolation createRuleViolation(String description) {
        RuleViolation violation = super.createRuleViolation(description);
        if (violation instanceof ParametricRuleViolation<?>) {
            ((ParametricRuleViolation<?>) violation).setRawCodeSnippet(getExampleCodeSnippet());
        }
        return violation;
    }
}
