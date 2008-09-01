/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.codesize;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.design.ExcessiveLengthRule;

/**
 * This rule detects when a method exceeds a certain
 * threshold.  i.e. if a method has more than x lines
 * of code.
 */
public class ExcessiveMethodLengthRule extends ExcessiveLengthRule {
    public ExcessiveMethodLengthRule() {
        super(ASTMethodDeclaration.class);
        setProperty(MINIMUM_DESCRIPTOR, 100d);
    }
}
