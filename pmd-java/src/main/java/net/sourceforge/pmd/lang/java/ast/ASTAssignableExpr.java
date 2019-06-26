/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * An expression that may be assigned by an {@linkplain ASTAssignmentExpression assignment expression},
 * or incremented or decremented. In the JLS, the result of such expressions
 * is a <i>variable</i>, while other expressions evaluate to a <i>value</i>.
 * The distinction is equivalent to C-world <i>lvalue</i>, <i>rvalue</i>.
 *
 *
 * <pre class="grammar">
 *
 * AssignableExpr ::= {@link ASTVariableReference VariableReference}
 *                  | {@link ASTFieldAccess FieldAccess}
 *                  | {@link ASTArrayAccess ArrayAccess}
 *
 * </pre>
 *
 * @author Clément Fournier
 */
public interface ASTAssignableExpr extends ASTPrimaryExpression {

    /**
     * Returns how this expression is accessed in the enclosing expression.
     */
    @NonNull
    default AccessType getAccessType() {

        Node parent = this.jjtGetParent();

        boolean isIncOrDec = parent instanceof ASTPreDecrementExpression
            || parent instanceof ASTPreIncrementExpression
            || parent instanceof ASTPostfixExpression;

        if (isIncOrDec || jjtGetChildIndex() == 0 && parent instanceof ASTAssignmentExpression) {
            return AccessType.WRITE;
        }

        return AccessType.READ;
    }

}
