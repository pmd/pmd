/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Those are some interfaces that are not published, but are used to keep
 * uniform names on related concepts. Maybe it makes sense to publish some of
 * them at some point.
 */
@SuppressWarnings("PMD.MissingStaticMethodInNonInstantiatableClass")
final class InternalInterfaces {

    private InternalInterfaces() {
        // utility class
    }

    interface OperatorLike {


        /**
         * Returns the token used to represent the type in source
         * code, e.g. {@code "+"} or {@code "*"}.
         */
        String getToken();

    }

    /** Just to share the method names. */
    interface BinaryExpressionLike extends ASTExpression {

        /** Returns the left-hand-side operand. */
        default ASTExpression getLeftOperand() {
            return (ASTExpression) jjtGetChild(0);
        }


        /** Returns the right-hand side operand. */
        default ASTExpression getRightOperand() {
            return (ASTExpression) jjtGetChild(1);
        }


        /** Returns the operator. */
        OperatorLike getOperator();
    }

    /**
     * Node that may be qualified by an expression, e.g. an instance method call or
     * inner class constructor invocation. This also works for {@link ASTExplicitConstructorInvocation}
     * which is why this interface does not implement {@link ASTExpression}.
     */
    interface QualifierOwner extends JavaNode {

        /**
         * Returns the expression to the left of the "." if it exists.
         * This may be a {@link ASTTypeExpression type expression}, or
         * an {@link ASTAmbiguousName ambiguous name}.
         */
        @Nullable
        default ASTExpression getQualifier() {
            return AstImplUtil.getChildAs(this, 0, ASTExpression.class);
        }
    }

    /**
     * Tags a node that has at least one child, then some methods never
     * return null.
     */
    interface AtLeastOneChild extends JavaNode {

        /** Returns the first child of this node, never null. */
        @Override
        @NonNull
        default JavaNode getFirstChild() {
            assert jjtGetNumChildren() > 0;
            return jjtGetChild(0);
        }


        /** Returns the last child of this node, never null. */
        @Override
        @NonNull
        default JavaNode getLastChild() {
            assert jjtGetNumChildren() > 0;
            return jjtGetChild(jjtGetNumChildren() - 1);
        }
    }

}
