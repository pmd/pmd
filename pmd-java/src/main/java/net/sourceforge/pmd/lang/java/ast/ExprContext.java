/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;

/**
 * Context of an expression. This determines the target type of poly
 * expressions, which is necessary for overload resolution. It also
 * determines what kinds of conversions apply to the value to make it
 * compatible with the context.
 */
@Experimental("The API is minimal until more use cases show up, and this is better tested.")
public abstract class ExprContext {
    // note: most members of this class are quite low-level and should
    // stay package-private for exclusive use by PolyResolution.

    final CtxKind kind;

    private ExprContext(CtxKind kind) {
        this.kind = kind;
    }

    /**
     * Returns the target type, or null if there is none or it couldn't
     * be determined reliably.
     */
    // note: only meant for public use by rules
    // note: this may triggers type resolution of the context
    public abstract @Nullable JTypeMirror getTargetType();

    /**
     * Returns true if this context does not provide any target type.
     * This is then a sentinel object.
     */
    public boolean isMissing() {
        return kind == CtxKind.Missing;
    }

    /**
     * If true this is an invocation context. This means, the target
     * type may depend on overload resolution.
     */
    public boolean isInvocationContext() {
        return kind == CtxKind.Invocation;
    }

    public boolean isCastContext() {
        return kind == CtxKind.Cast;
    }

    public boolean isSpecialTernaryContext() {
        return kind == CtxKind.Ternary;
    }

    boolean canGiveContextToPoly(boolean lambda) {
        return true;
    }

    static ExprContext newAssignmentCtx(JTypeMirror targetType) {
        return new RegularCtx(targetType, CtxKind.Assignment);
    }

    static ExprContext newNumericCtx(JPrimitiveType targetType) {
        return new RegularCtx(targetType, CtxKind.Numeric);
    }

    static ExprContext newCastCtx(JTypeMirror targetType) {
        return new RegularCtx(targetType, CtxKind.Cast);
    }

    static ExprContext newSuperCtorCtx(JTypeMirror superclassType) {
        return new RegularCtx(superclassType, CtxKind.Other);
    }

    static ExprContext newStandaloneTernaryCtx(JTypeMirror ternaryType) {
        return new RegularCtx(ternaryType, CtxKind.Other);
    }

    static final class InvocCtx extends ExprContext {

        final int arg;
        final InvocationNode node;

        InvocCtx(int arg, InvocationNode node) {
            super(CtxKind.Invocation);
            this.arg = arg;
            this.node = node;
        }

        @Override
        public @Nullable JTypeMirror getTargetType() {
            // this triggers type resolution of the enclosing expr.
            OverloadSelectionResult overload = node.getOverloadSelectionInfo();
            if (overload.isFailed()) {
                return null;
            }
            return overload.ithFormalParam(arg);
        }

        @Override
        public boolean isMissing() {
            return false;
        }

        @Override
        public boolean isInvocationContext() {
            return true;
        }
    }

    /**
     * Kind of context.
     */
    enum CtxKind {
        /**
         * Invocation context (method arguments).
         */
        Invocation,

        /**
         * Assignment context, eg:
         * <ul>
         * <li>RHS of an assignment
         * <li>Return statement
         * <li>Array initializer
         * </ul>
         *
         * <p>An assignment context flows through ternary/switch branches.
         */
        Assignment,

        /**
         * Cast context. Lambdas and method refs can use them as a
         * target type, but no other expressions. Cast contexts do not
         * flow through ternary/switch branches.
         */
        Cast,

        /**
         * Numeric context. Does not provide a target type for poly expressions.
         * For standalone expressions, they may determine that an (un)boxing or
         * primitive widening conversion occurs. Numeric contexts do not flow
         * through ternary/switch branches.
         *
         * <p>For instance:
         * <pre>{@code
         * Integer integer;
         *
         * array[integer] // Integer is unboxed to int
         * integer + 1    // Integer is unboxed to int
         * 0 + 1.0        // int (left) is widened to double
         * integer + 1.0  // Integer is unboxed to int, then widened to double
         * }</pre>
         */
        Numeric,

        /** Kind for a standalone ternary (both branches are then in this context). */
        Ternary,

        /** Kind for a missing context ({@link RegularCtx#NO_CTX}). */
        Missing,

        /** Other kinds of situation that have a target type. */
        Other,
    }

    static final class RegularCtx extends ExprContext {

        static final RegularCtx NO_CTX = new RegularCtx(null, CtxKind.Missing);

        final @Nullable JTypeMirror targetType;

        RegularCtx(@Nullable JTypeMirror targetType, CtxKind kind) {
            super(kind);
            assert kind != CtxKind.Invocation;
            this.targetType = targetType;
        }


        @Override
        public boolean canGiveContextToPoly(boolean lambdaOrMethodRef) {
            return kind == CtxKind.Cast ? lambdaOrMethodRef
                                        : kind == CtxKind.Assignment;
        }

        /**
         * Returns the target type bestowed by this context ON A POLY EXPRESSION.
         *
         * @param allowCasts Whether cast contexts should be considered,
         *                   if false, and this is a cast ctx, returns null.
         */
        @Nullable JTypeMirror getPolyTargetType(boolean allowCasts) {
            if (!allowCasts && kind == CtxKind.Cast || kind == CtxKind.Numeric) {
                return null;
            }
            return targetType;
        }

        @Override
        public @Nullable JTypeMirror getTargetType() {
            return targetType;
        }
    }
}
