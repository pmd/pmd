/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import net.sourceforge.pmd.lang.plsql.ast.ExecutableCode;

/**
 * This rule detects when a method exceeds a certain threshold. i.e. if a method
 * has more than x lines of code.
 */
public class ExcessiveMethodLengthRule extends ExcessiveLengthRule {
    public ExcessiveMethodLengthRule() {
        super(ExecutableCode.class);
        setProperty(MINIMUM_DESCRIPTOR, 100d);
    }
}
