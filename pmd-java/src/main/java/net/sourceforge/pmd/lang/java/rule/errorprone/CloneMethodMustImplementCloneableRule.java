/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * The method clone() should only be implemented if the class implements the
 * Cloneable interface with the exception of a final method that only throws
 * CloneNotSupportedException. This version uses PMD's type resolution
 * facilities, and can detect if the class implements or extends a Cloneable
 * class
 *
 * @author acaplan
 */
public class CloneMethodMustImplementCloneableRule extends AbstractJavaRulechainRule {

    public CloneMethodMustImplementCloneableRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(final ASTMethodDeclaration node, final Object data) {
        if (!isCloneMethod(node)) {
            return data;
        }
        ASTBlock body = node.getBody();
        if (body != null && justThrowsCloneNotSupported(body)) {
            return data;
        }

        ASTAnyTypeDeclaration type = node.getEnclosingType();
        if (type instanceof ASTClassOrInterfaceDeclaration && !TypeTestUtil.isA(Cloneable.class, type)) {
            // Nothing can save us now
            addViolation(data, node);
        }
        return data;
    }

    private static boolean justThrowsCloneNotSupported(ASTBlock body) {
        if (body.size() != 1) {
            return false;
        }
        return body.getChild(0)
                   .asStream()
                   .filterIs(ASTThrowStatement.class)
                   .map(ASTThrowStatement::getExpr)
                   .filter(it -> TypeTestUtil.isA(CloneNotSupportedException.class, it))
                   .nonEmpty();
    }

    private static boolean isCloneMethod(final ASTMethodDeclaration method) {
        return "clone".equals(method.getName()) && method.getFormalParameters().size() == 0;
    }
}
