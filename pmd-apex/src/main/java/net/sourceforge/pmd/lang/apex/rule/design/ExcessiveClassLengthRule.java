/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.internal.AbstractCounterCheckRule;

/**
 * This rule detects when a class exceeds a certain threshold. i.e. if a class
 * has more than 1000 lines of code.
 */
public class ExcessiveClassLengthRule extends AbstractCounterCheckRule.AbstractLineLengthCheckRule<ASTUserClass> {

    public ExcessiveClassLengthRule() {
        super(ASTUserClass.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 1000;
    }

    @Override
    protected boolean isIgnored(ASTUserClass node) {
        return node.getModifiers().isTest();
    }
}
