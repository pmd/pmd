/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;

/**
 * Context of an expression. This determines its target type, which is necessary for overload resolution.
 */
@Experimental
public abstract class ExprContext {
    // note: most members of this class are quite low-level and should
    // stay package-private for exclusive use by PolyResolution.

    private ExprContext() {
        // sealed class
    }

    /**
     * Returns the target type, or null if there is none or it couldn't
     * be determined reliably.
     */
    // note: only meant for public use by rules
    // note: this may triggers type resolution of the context
    public @Nullable JTypeMirror getTargetType() {
        return getTargetTypeAfterResolution();
    }

    /**
     * If true this is an invocation context. This means, the target
     * type may depend on overload resolution.
     */
    public boolean isInvocationContext() {
        return false;
    }

    static ExprContext newAssignmentCtx(JTypeMirror targetType) {
        return new RegularCtx(targetType, CtxKind.Assignment);
    }

    static ExprContext newCastCtx(JTypeMirror targetType) {
        return new RegularCtx(targetType, CtxKind.Cast);
    }

    static ExprContext newSuperCtorCtx(JTypeMirror superclassType) {
        return new RegularCtx(superclassType, CtxKind.Other);
    }

    abstract @Nullable JTypeMirror getTargetTypeAfterResolution();

    static final class InvocCtx extends ExprContext {

        final int arg;
        final InvocationNode node;

        InvocCtx(int arg, InvocationNode node) {
            this.arg = arg;
            this.node = node;
        }

        @Override
        @Nullable JTypeMirror getTargetTypeAfterResolution() {
            // this triggers type resolution of the enclosing expr.
            OverloadSelectionResult overload = node.getOverloadSelectionInfo();
            if (overload.isFailed()) {
                return null;
            }
            return overload.ithFormalParam(arg);
        }

        @Override
        public boolean isInvocationContext() {
            return true;
        }
    }

    enum CtxKind {
        /**
         * Assignment context, eg:
         * <ul>
         * <li>RHS of an assignment
         * <li>Return statement
         * <li>Array initializer
         * </ul>
         */
        Assignment,

        /**
         * Cast context. Lambdas can use them as target type, but not
         * eg conditional expressions.
         */
        Cast,

        /** Other kinds of situation that have a target type (eg {@link RegularCtx#NO_CTX}). */
        Other,
    }

    static final class RegularCtx extends ExprContext {

        static final RegularCtx NO_CTX = new RegularCtx(null, CtxKind.Other);

        final @Nullable JTypeMirror targetType;
        final CtxKind kind;

        RegularCtx(@Nullable JTypeMirror targetType, CtxKind kind) {
            this.targetType = targetType;
            this.kind = kind;
        }

        /**
         * Returns the target type bestowed by this context ON A POLY EXPRESSION.
         *
         * @param allowCasts Whether cast contexts should be considered,
         *                   if false, and this is a cast ctx, returns null.
         */
        @Nullable JTypeMirror getTargetType(boolean allowCasts) {
            if (!allowCasts && kind == CtxKind.Cast) {
                return null;
            }
            return targetType;
        }

        @Override
        @Nullable JTypeMirror getTargetTypeAfterResolution() {
            return targetType;
        }
    }
}
