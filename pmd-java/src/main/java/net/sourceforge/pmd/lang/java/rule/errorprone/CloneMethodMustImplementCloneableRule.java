/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * The method clone() should only be implemented if the class implements the
 * Cloneable interface with the exception of a final method that only throws
 * CloneNotSupportedException. This version uses PMD's type resolution
 * facilities, and can detect if the class implements or extends a Cloneable
 * class
 *
 * @author acaplan
 */
public class CloneMethodMustImplementCloneableRule extends AbstractJavaRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(final ASTMethodDeclaration node, final Object data) {
        if (node.isAbstract() || !isCloneMethod(node)) {
            return data;
        } else if (justThrowsCloneNotSupported(node)) {
            return data;
        }

        ASTAnyTypeDeclaration type = node.getEnclosingType();
        if (type instanceof ASTClassOrInterfaceDeclaration && !TypeHelper.isA(type, Cloneable.class)) {
            // Nothing can save us now
            addViolation(data, node);
        }
        return data;
    }

    private static boolean justThrowsCloneNotSupported(ASTMethodDeclaration decl) {
        ASTBlock body = decl.getBody();
        if (body.size() != 1) {
            return false;
        }
        return body.getChild(0)
                   .asStream()
                   .filterIs(ASTThrowStatement.class)
                   .map(ASTThrowStatement::getExpr)
                   .filter(it -> TypeHelper.isA(it, CloneNotSupportedException.class))
                   .nonEmpty();
    }

    private static boolean isCloneMethod(final ASTMethodDeclaration method) {
        return "clone".equals(method.getName()) && method.getFormalParameters().size() == 0;
    }
}
