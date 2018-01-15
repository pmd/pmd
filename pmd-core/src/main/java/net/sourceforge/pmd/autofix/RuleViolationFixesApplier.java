/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix;

import java.util.LinkedList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Stores and applies, in order of arrival, insert / modify / delete operations over AST nodes.
 */
public class RuleViolationFixesApplier {
    private final List<RuleViolationFixContext> ruleViolationFixContexts = new LinkedList<>();
    private boolean applied = false;

    /**
     * Add a new rule violation fix with its corresponding node.
     *
     * @param ruleViolationFixClass the class to use when attempting to fix the node
     * @param nodeToFix the node on which to apply operations
     */
    public void addRuleViolationFix(final Class<? extends RuleViolationFix> ruleViolationFixClass, final Node nodeToFix) {
        assertFixesHaveNotBeenApplied();
        ruleViolationFixContexts.add(new RuleViolationFixContext(ruleViolationFixClass, nodeToFix));
    }

    /**
     * Apply, in order of insertion, the auto fixes for the nodes.
     */
    public void applyAutoFixes() {
        assertFixesHaveNotBeenApplied();
        applied = true;
        for (final RuleViolationFixContext ruleViolationFixContext : ruleViolationFixContexts) {
            ruleViolationFixContext.applyFixToNode();
        }
    }

    /**
     * Apply, in order of insertion, the auto fixes for the nodes. In the end it calls the {@link #clear()} method.
     */
    public void applyAutoFixesAndClear() {
        applyAutoFixes();
        clear();
    }

    /**
     * Make sure that the fixes of the current list have not been applied.
     */
    private void assertFixesHaveNotBeenApplied() {
        if (applied) {
            throw new IllegalStateException("Fixes have already been applied");
        }
    }

    /**
     * Allows for new insertion of operations and later application of them without needing to create a new
     * {@link RuleViolationFixesApplier}.
     */
    public void clear() {
        ruleViolationFixContexts.clear();
        applied = false;
    }
}
