/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolationFix;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * This class handles of producing a Language specific RuleViolation and adding
 * to a Report.
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

    /**
     * Create a rule violation with the appropriate fixer. Note that the rule is dissociated from the fixer class.
     *
     * @param ruleContext the context of the rule at the moment of the violation
     * @param rule the rule which reported the violation
     * @param node the node in which the fixes to that rule violation will be applied
     * @param message the message to put in the report
     * @param autoFixerClass the class in which the fixes to that rule violation will be applied
     */
    void addViolationWithAutoFixer(RuleContext ruleContext, Rule rule, Node node, String message,
                                   Class<? extends RuleViolationFix> autoFixerClass);
}
