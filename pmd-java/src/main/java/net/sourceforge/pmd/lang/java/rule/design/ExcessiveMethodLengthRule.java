/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;


/**
 * This rule detects when a method exceeds a certain threshold. i.e. if a method
 * has more than x lines of code.
 */
public class ExcessiveMethodLengthRule extends ExcessiveLengthRule {
    public ExcessiveMethodLengthRule() {
        super(ASTMethodOrConstructorDeclaration.class);
        setProperty(MINIMUM_DESCRIPTOR, 100d);
    }
}
