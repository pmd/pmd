/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.ast.JModifier.FINAL;
import static net.sourceforge.pmd.lang.java.ast.JModifier.PUBLIC;
import static net.sourceforge.pmd.lang.java.ast.JModifier.STATIC;

import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner;
import net.sourceforge.pmd.lang.java.rule.internal.AbstractJavaCounterCheckRule;

/**
 * Rule attempts to count all public methods and public attributes
 * defined in a class.
 *
 * <p>If a class has a high number of public operations, it might be wise
 * to consider whether it would be appropriate to divide it into
 * subclasses.</p>
 *
 * <p>A large proportion of public members and operations means the class
 * has high potential to be affected by external classes. Furthermore,
 * increased effort will be required to thoroughly test the class.
 * </p>
 *
 * @author aglover
 */
public class ExcessivePublicCountRule extends AbstractJavaCounterCheckRule<ASTTypeDeclaration> {

    public ExcessivePublicCountRule() {
        super(ASTTypeDeclaration.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 45;
    }

    /**
     * @deprecated since 7.18.0
     */
    @Override
    protected boolean isViolation(ASTTypeDeclaration node, int reportLevel) {
        return super.isViolation(node, reportLevel);
    }

    @Override
    protected int getMetric(ASTTypeDeclaration node) {
        return node.getDeclarations()
                   .filterIs(ModifierOwner.class)
                   .filter(it -> it.hasModifiers(PUBLIC))
                   // filter out constants
                   .filter(it -> !(it instanceof ASTFieldDeclaration && it.hasModifiers(STATIC, FINAL)))
                   .count();
    }
}
