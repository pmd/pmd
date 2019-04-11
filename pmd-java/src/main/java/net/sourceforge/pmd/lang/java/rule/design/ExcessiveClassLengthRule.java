/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.rule.internal.AbstractJavaCounterCheckRule;

/**
 * This rule detects when a class exceeds a certain threshold. i.e. if a class
 * has more than 1000 lines of code.
 */
public class ExcessiveClassLengthRule extends AbstractJavaCounterCheckRule.AbstractLineLengthCheckRule<ASTAnyTypeDeclaration> {
    public ExcessiveClassLengthRule() {
        super(ASTAnyTypeDeclaration.class,
              ASTEnumDeclaration.class,
              ASTClassOrInterfaceDeclaration.class,
              ASTAnnotationTypeDeclaration.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 1000;
    }
}
