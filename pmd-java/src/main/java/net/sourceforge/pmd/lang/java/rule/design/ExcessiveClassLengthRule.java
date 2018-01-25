/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;

/**
 * This rule detects when a class exceeds a certain threshold. i.e. if a class
 * has more than 1000 lines of code.
 */
public class ExcessiveClassLengthRule extends ExcessiveLengthRule {
    public ExcessiveClassLengthRule() {
        super(ASTAnyTypeDeclaration.class);
        setProperty(MINIMUM_DESCRIPTOR, 1000d);
    }
}
