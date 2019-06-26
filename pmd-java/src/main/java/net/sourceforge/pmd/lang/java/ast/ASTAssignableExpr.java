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
 * AssignableExpr ::= {@link ASTVariableAccess VariableAccess}
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
     * If this expression occurs as the left-hand-side of an {@linkplain ASTAssignmentExpression assignment},
     * or as the target of an {@linkplain ASTIncrementExpression increment or decrement expression},
     * this method returns {@link AccessType#WRITE}. Otherwise the value is just {@linkplain AccessType#READ read}.
     */
    @NonNull
    default AccessType getAccessType() {

        Node parent = this.jjtGetParent();

        if (parent instanceof ASTIncrementExpression
            || jjtGetChildIndex() == 0 && parent instanceof ASTAssignmentExpression) {
            return AccessType.WRITE;
        }

        return AccessType.READ;
    }


    /**
     * Represents the type of access of an {@linkplain ASTAssignableExpr assignable expression}.
     */
    enum AccessType {

        /** The value of the variable is read. */
        READ,

        /** The value is written to, possibly being read before or after. */
        WRITE
    }
}
