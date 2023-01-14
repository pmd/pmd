/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import java.util.Iterator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;

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
        @NonNull
        default ASTExpression getLeftOperand() {
            return (ASTExpression) getChild(0);
        }


        /** Returns the right-hand side operand. */
        @NonNull
        default ASTExpression getRightOperand() {
            return (ASTExpression) getChild(1);
        }


        /** Returns the operator. */
        @NonNull
        OperatorLike getOperator();
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
            assert getNumChildren() > 0;
            return getChild(0);
        }


        /** Returns the last child of this node, never null. */
        @Override
        @NonNull
        default JavaNode getLastChild() {
            assert getNumChildren() > 0;
            return getChild(getNumChildren() - 1);
        }
    }

    interface AllChildrenAreOfType<T extends JavaNode> extends JavaNode {

        @Override
        @Nullable
        default T getFirstChild() {
            if (getNumChildren() == 0) {
                return null;
            }
            return (T) getChild(0);
        }


        @Override
        @Nullable
        default T getLastChild() {
            if (getNumChildren() == 0) {
                return null;
            }
            return (T) getChild(getNumChildren() - 1);
        }
    }

    /**
     * Tags a node that has at least one child, then some methods never
     * return null.
     */
    interface AtLeastOneChildOfType<T extends JavaNode> extends AllChildrenAreOfType<T> {

        /** Returns the first child of this node, never null. */
        @Override
        @NonNull
        default T getFirstChild() {
            assert getNumChildren() > 0 : "No children for node implementing AtLeastOneChild " + this;
            return (T) getChild(0);
        }


        /** Returns the last child of this node, never null. */
        @Override
        @NonNull
        default T getLastChild() {
            assert getNumChildren() > 0 : "No children for node implementing AtLeastOneChild " + this;
            return (T) getChild(getNumChildren() - 1);
        }
    }

    interface VariableIdOwner extends JavaNode {

        /** Returns the id of the declared variable. */
        ASTVariableDeclaratorId getVarId();
    }

    interface MultiVariableIdOwner extends JavaNode, Iterable<ASTVariableDeclaratorId>, AccessNode {

        /**
         * Returns a stream of the variable ids declared
         * by this node.
         */
        default NodeStream<ASTVariableDeclaratorId> getVarIds() {
            return children(ASTVariableDeclarator.class).children(ASTVariableDeclaratorId.class);
        }


        @Override
        default Iterator<ASTVariableDeclaratorId> iterator() {
            return getVarIds().iterator();
        }

        ASTType getTypeNode();
    }

}
