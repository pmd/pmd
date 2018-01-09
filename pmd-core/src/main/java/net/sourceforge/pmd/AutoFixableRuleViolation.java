/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

/**
 * This interface allows to determine which rule violations are fixable, and with which class the fixes will be made.
 */
public interface AutoFixableRuleViolation extends RuleViolation {

    /**
     * Obtain the class which will attempt to fix the AST.
     * @return
     */
    Class<? extends RuleViolationFix> getRuleViolationFixer();
}
