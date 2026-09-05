/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.ast.NodeStream.DescendantNodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
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
        if (!JavaAstUtils.isCloneMethod(node)) {
            return data;
        }
        ASTBlock body = node.getBody();
        if (body != null && justThrowsCloneNotSupported(body)) {
            return data;
        }

        ASTTypeDeclaration type = node.getEnclosingType();
        if (type instanceof ASTClassDeclaration && !TypeTestUtil.isA(Cloneable.class, type)) {
            // Nothing can save us now
            asCtx(data).addViolation(node);
        }
        return data;
    }

    /**
     * A method body "just throws CloneNotSupportedException" if it never
     * returns a value and every {@code throw} it contains (however it is
     * reached, e.g. via a local variable, a logging call beforehand, or a
     * try/catch) throws only {@link CloneNotSupportedException}. A body with
     * no throw statement at all doesn't count - it doesn't actually disable
     * cloning. Nested classes and lambdas are not considered, as their
     * control flow is independent of the enclosing method's.
     */
    private static boolean justThrowsCloneNotSupported(ASTBlock body) {
        if (body.descendants(ASTReturnStatement.class).nonEmpty()) {
            return false;
        }
        DescendantNodeStream<ASTThrowStatement> throwStatements = body.descendants(ASTThrowStatement.class);
        return throwStatements.nonEmpty()
            && throwStatements.all(it -> TypeTestUtil.isA(CloneNotSupportedException.class, it.getExpr()));
    }

}
