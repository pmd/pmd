/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


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
        default ASTExpression getLhs() {
            return (ASTExpression) jjtGetChild(0);
        }


        /** Returns the right-hand side operand. */
        default ASTExpression getRhs() {
            return (ASTExpression) jjtGetChild(1);
        }


        /** Returns the operator. */
        OperatorLike getOperator();
    }

    /**
     * Node that may be qualified by an expression, e.g. an instance method call or
     * inner class constructor invocation.
     */
    interface ASTQualifiableExpression extends ASTExpression {

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
     * A node that has a single type of children, but can have no children.
     *
     * Package private, the methods are exposed nevertheless.
     *
     * @author Clément Fournier
     */
    interface JSingleChildNode<T extends JavaNode> extends JavaNode {


        @Override
        @Nullable
        default T getLastChild() {
            return jjtGetNumChildren() > 0 ? jjtGetChild(jjtGetNumChildren() - 1) : null;

        }


        @Override
        @Nullable
        default T getFirstChild() {
            return jjtGetNumChildren() > 0 ? jjtGetChild(0) : null;
        }


        @Override
        T jjtGetChild(int index);
    }
}
