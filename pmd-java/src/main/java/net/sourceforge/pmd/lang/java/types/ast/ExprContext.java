/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.ast;

import static net.sourceforge.pmd.lang.java.types.ast.ExprContext.ExprContextKind.ASSIGNMENT;
import static net.sourceforge.pmd.lang.java.types.ast.ExprContext.ExprContextKind.CAST;
import static net.sourceforge.pmd.lang.java.types.ast.ExprContext.ExprContextKind.INVOCATION;
import static net.sourceforge.pmd.lang.java.types.ast.ExprContext.ExprContextKind.MISSING;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypeConversion;
import net.sourceforge.pmd.util.AssertionUtil;

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

    final ExprContextKind kind;

    private ExprContext(ExprContextKind kind) {
        this.kind = kind;
    }

    /**
     * Returns the target type, or null if {@linkplain #isMissing() there is none}.
     */
    // note: only meant for public use by rules
    // note: this may triggers type resolution of the context
    public abstract @Nullable JTypeMirror getTargetType();


    /**
     * Returns true if the given type is compatible with this context
     * implicitly (without cast). Conversions may occur to make this
     * possible. What conversions may occur depends on the kind of
     * this context.
     *
     * <p>By convention, any type is compatible with a missing context.
     *
     * @param type A type which is checked against the target type
     */
    public boolean acceptsType(@NonNull JTypeMirror type) {
        AssertionUtil.requireParamNotNull("type", type);

        JTypeMirror targetType = getTargetType();

        return targetType == null
                // todo there's a gritty detail about compound assignment operators
                //  with a primitive LHS, see https://github.com/pmd/pmd/issues/2023
                || (kind == CAST ? TypeConversion.isConvertibleInCastContext(type, targetType)
                            : TypeConversion.isConvertibleUsingBoxing(type, targetType));
    }

    /**
     * Returns true if this context does not provide any target type.
     * This is then a sentinel object.
     */
    public boolean isMissing() {
        return kind == MISSING;
    }

    /** Returns the kind of context this is. */
    public ExprContextKind getKind() {
        return kind;
    }

    final boolean canGiveContextToPoly(boolean lambdaOrMethodRef) {
        return this.hasKind(ASSIGNMENT)
            || this.hasKind(INVOCATION)
            || this.hasKind(CAST) && lambdaOrMethodRef;
    }

    public @Nullable InvocationNode getInvocNodeIfInvocContext() {
        return this instanceof InvocCtx ? ((InvocCtx) this).node : null;
    }

    public @NonNull ExprContext getToplevelCtx() {
        return this; // todo
    }

    /**
     * Returns the target type bestowed by this context ON A POLY EXPRESSION.
     *
     * @param lambdaOrMethodRef Whether the poly to be considered is a
     *                          lambda or method ref. In this case, cast
     *                          contexts can give a target type.
     */
    public @Nullable JTypeMirror getPolyTargetType(boolean lambdaOrMethodRef) {
        if (!canGiveContextToPoly(lambdaOrMethodRef)) {
            return null;
        }
        return getTargetType();
    }

    static ExprContext newOtherContext(@NonNull JTypeMirror targetType, ExprContextKind kind) {
        AssertionUtil.requireParamNotNull("target type", targetType);
        return new RegularCtx(targetType, kind);
    }

    static ExprContext newInvocContext(InvocationNode invocNode, int argumentIndex) {
        return new InvocCtx(argumentIndex, invocNode);
    }

    /**
     * Returns an {@link ExprContext} instance which represents a
     * missing context. Use {@link #isMissing()} instead of testing
     * for equality.
     */
    public static RegularCtx getMissingInstance() {
        return RegularCtx.NO_CTX;
    }

    public boolean hasKind(ExprContextKind kind) {
        return getKind() == kind;
    }

    private static final class InvocCtx extends ExprContext {

        final int arg;
        final InvocationNode node;

        InvocCtx(int arg, InvocationNode node) {
            super(INVOCATION);
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
        public String toString() {
            return "InvocCtx{arg=" + arg + ", node=" + node + '}';
        }
    }

    /**
     * Kind of context.
     */
    public enum ExprContextKind {
        /** Invocation context (method arguments). */
        INVOCATION,

        /**
         * Assignment context. This includes:
         * <ul>
         * <li>RHS of an assignment
         * <li>Return statement
         * <li>Array initializer
         * <li>Superclass constructor invocation
         * </ul>
         *
         * <p>An assignment context flows through ternary/switch branches.
         * They are a context for poly expressions.
         */
        ASSIGNMENT,

        /**
         * Cast context. Lambdas and method refs can use them as a
         * target type, but no other expressions. Cast contexts do not
         * flow through ternary/switch branches.
         */
        CAST,

        /**
         * Numeric context. May determine that an (un)boxing or
         * primitive widening conversion occurs. These is the context for
         * operands of arithmetic expressions, array indices.
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
        NUMERIC,

        /**
         * String contexts, which convert the operand to a string using {@link String#valueOf(Object)},
         * or the equivalent for a primitive type. They accept operands of any type.
         * This is the context for the operands of a string concatenation expression,
         * and for the message of an assert statement.
         */
        STRING,

        /** Kind for a standalone ternary (both branches are then in this context). */
        TERNARY,

        /** Kind for a missing context ({@link RegularCtx#NO_CTX}). */
        MISSING,

        /**
         * Boolean contexts, which unbox their operand to a boolean.
         * They accept operands of type boolean or Boolean. This is the
         * context for e.g. the condition of an {@code if} statement, an
         * assert statement, etc.
         *
         * <p>This provides a target type for conversions, but not for poly
         * expressions.
         */
        BOOLEAN,
    }

    static final class RegularCtx extends ExprContext {

        private static final RegularCtx NO_CTX = new RegularCtx(null, MISSING);

        final @Nullable JTypeMirror targetType;

        RegularCtx(@Nullable JTypeMirror targetType, ExprContextKind kind) {
            super(kind);
            assert kind != INVOCATION;
            this.targetType = targetType;
        }

        @Override
        public @Nullable JTypeMirror getTargetType() {
            return targetType;
        }

        @Override
        public String toString() {
            return "RegularCtx{kind=" + kind + ", targetType=" + targetType + '}';
        }
    }
}
