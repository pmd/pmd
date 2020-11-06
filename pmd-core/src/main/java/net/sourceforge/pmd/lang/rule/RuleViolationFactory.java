/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * This class handles of producing a Language specific RuleViolation and adding
 * to a Report.
 *
 * <p>Since PMD 6.21.0, implementations of this interface are considered internal
 * API and hence deprecated. Clients should exclusively use this interface and obtain
 * instances through {@link LanguageVersionHandler#getRuleViolationFactory()}.
 */
public interface RuleViolationFactory {
    /**
     * Adds a violation to the report.
     *
     * @param ruleContext
     *            the RuleContext
     * @param rule
     *            the rule
     * @param node
     *            the node that produces the violation
     * @param message
     *            specific message to put in the report
     * @param args
     *            arguments to embed in the rule violation message
     */
    void addViolation(RuleContext ruleContext, Rule rule, Node node, String message, Object[] args);

    void addViolation(RuleContext ruleContext, Rule rule, Node node, String message, int beginLine, int endLine,
            Object[] args);
}
