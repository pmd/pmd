/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.rule.internal.AbstractJavaCounterCheckRule;

/**
 * This rule detects when a class exceeds a certain threshold. i.e. if a class
 * has more than 1000 lines of code.
 *
 * @deprecated Use {@link NcssCountRule} instead.
 */
@Deprecated
public class ExcessiveClassLengthRule extends AbstractJavaCounterCheckRule.AbstractLineLengthCheckRule<ASTAnyTypeDeclaration> {
    public ExcessiveClassLengthRule() {
        super(ASTAnyTypeDeclaration.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 1000;
    }
}
