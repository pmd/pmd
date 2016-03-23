/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.codesize;

import net.sourceforge.pmd.lang.apex.ast.ASTCompilation;
import net.sourceforge.pmd.lang.apex.rule.design.ExcessiveLengthRule;

/**
 * This rule detects when a class exceeds a certain
 * threshold.  i.e. if a class has more than 1000 lines
 * of code.
 */
public class ExcessiveClassLengthRule extends ExcessiveLengthRule {
    public ExcessiveClassLengthRule() {
        super(ASTCompilation.class);
        setProperty(MINIMUM_DESCRIPTOR, 1000d);
    }
}
