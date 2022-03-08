/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import java.util.Iterator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;

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
        T getChild(int index);

        @Override
        @Nullable
        default T getFirstChild() {
            if (getNumChildren() == 0) {
                return null;
            }
            return getChild(0);
        }


        @Override
        @Nullable
        default T getLastChild() {
            if (getNumChildren() == 0) {
                return null;
            }
            return getChild(getNumChildren() - 1);
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
            return getChild(0);
        }


        /** Returns the last child of this node, never null. */
        @Override
        @NonNull
        default T getLastChild() {
            assert getNumChildren() > 0 : "No children for node implementing AtLeastOneChild " + this;
            return getChild(getNumChildren() - 1);
        }
    }

    interface VariableIdOwner extends JavaNode {

        /** Returns the id of the declared variable. */
        ASTVariableDeclaratorId getVarId();
    }

    interface MultiVariableIdOwner extends JavaNode, Iterable<ASTVariableDeclaratorId>, FinalizableNode {

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

    /**
     * An {@link AccessNode} that is not a local declaration, and can receive
     * a visibility modifier. Has a couple more convenient methods.
     */
    interface NonLocalDeclarationNode extends AccessNode {


        /**
         * Returns true if this declaration has a static modifier, implicitly or explicitly.
         */
        default boolean isStatic() {
            return hasModifiers(JModifier.STATIC);
        }


        // these are about visibility


        /** Returns true if this node has private visibility. */
        @NoAttribute
        default boolean isPrivate() {
            return getVisibility() == Visibility.V_PRIVATE;
        }


        /** Returns true if this node has public visibility. */
        @NoAttribute
        default boolean isPublic() {
            return getVisibility() == Visibility.V_PUBLIC;
        }


        /** Returns true if this node has protected visibility. */
        @NoAttribute
        default boolean isProtected() {
            return getVisibility() == Visibility.V_PROTECTED;
        }


        /** Returns true if this node has package visibility. */
        @NoAttribute
        default boolean isPackagePrivate() {
            return getVisibility() == Visibility.V_PACKAGE;
        }
    }

    /**
     * A node that may have the final modifier.
     */
    static interface FinalizableNode extends AccessNode {


        /**
         * Returns true if this variable, method or class is final (even implicitly).
         */
        default boolean isFinal() {
            return hasModifiers(JModifier.FINAL);
        }

    }
}
