/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.codesize;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.rule.design.ExcessiveLengthRule;

/**
 * This rule detects when a class exceeds a certain
 * threshold.  i.e. if a class has more than 1000 lines
 * of code.
 */
public class ExcessiveClassLengthRule extends ExcessiveLengthRule {
    public ExcessiveClassLengthRule() {
        super(ASTClassOrInterfaceDeclaration.class);
        setProperty(MINIMUM_DESCRIPTOR, 1000d);
    }
}
