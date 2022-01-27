/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.JVariableSig;

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
     * or as the target of an {@linkplain ASTUnaryExpression increment or decrement expression},
     * this method returns {@link AccessType#WRITE}. Otherwise the value is just {@linkplain AccessType#READ read}.
     */
    default @NonNull AccessType getAccessType() {

        Node parent = this.getParent();

        if (parent instanceof ASTUnaryExpression && !((ASTUnaryExpression) parent).getOperator().isPure()
            || getIndexInParent() == 0 && parent instanceof ASTAssignmentExpression) {
            return AccessType.WRITE;
        }

        return AccessType.READ;
    }

    /**
     * An {@linkplain ASTAssignableExpr assignable expression} that has
     * a name, and refers to a symbol.
     *
     * <pre class="grammar">
     *
     * NamedAssignableExpr ::= {@link ASTVariableAccess VariableAccess}
     *                       | {@link ASTFieldAccess FieldAccess}
     *
     * </pre>
     */
    interface ASTNamedReferenceExpr extends ASTAssignableExpr {

        /**
         * Returns the name of the referenced variable.
         */
        String getName();

        // TODO, also figure out if it makes sense to have unresolved symbols for those
        //  using null would be simpler...

        /**
         * Returns the signature of the referenced variable. This is
         * relevant for fields, as they may be inherited from some
         * parameterized supertype.
         */
        @Nullable JVariableSig getSignature(); // TODO this is probably multiplying the api points for nothing. You have symbol + type with getTypeMirror and getReferencedSym

        /**
         * Returns the symbol referenced by this variable.
         */
        default @Nullable JVariableSymbol getReferencedSym() {
            JVariableSig sig = getSignature();
            return sig == null ? null : sig.getSymbol();
        }

    }


    /**
     * Represents the type of access of an {@linkplain ASTAssignableExpr assignable expression}.
     */
    enum AccessType {

        /** The value of the variable is read. */
        READ,

        /**
         * The value is written to, possibly being read before or after.
         * Also see {@link JavaRuleUtil#isVarAccessReadAndWrite(ASTNamedReferenceExpr)}.
         */
        WRITE
    }
}
