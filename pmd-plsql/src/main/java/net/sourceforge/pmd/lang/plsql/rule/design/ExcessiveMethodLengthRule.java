/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import net.sourceforge.pmd.lang.plsql.ast.ExecutableCode;

/**
 * This rule detects when a method exceeds a certain threshold. i.e. if a method
 * has more than x lines of code.
 */
public class ExcessiveMethodLengthRule extends AbstractCounterCheckRule.AbstractLineLengthCheckRule<ExecutableCode> {
    public ExcessiveMethodLengthRule() {
        super(ExecutableCode.class
        );
    }

    @Override
    protected int defaultReportLevel() {
        return 100;
    }
}
