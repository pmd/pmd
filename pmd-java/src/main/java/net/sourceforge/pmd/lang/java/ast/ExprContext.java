/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypeConversion;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.TypesFromReflection;

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
        if (targetType == null) {
            return true;
        }

        // todo there's a gritty detail about compound assignment operators
        //  with a primitive LHS, see https://github.com/pmd/pmd/issues/2023
        return isCastContext() ? TypeConversion.isConvertibleInCastContext(type, targetType)
                               : TypeConversion.isConvertibleUsingBoxing(type, targetType);
    }

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

    public boolean isNumeric() {
        return kind == CtxKind.Numeric;
    }

    public boolean isString() {
        return kind == CtxKind.String;
    }

    boolean canGiveContextToPoly(boolean lambda) {
        return true;
    }

    public boolean isTernary() {
        return kind == CtxKind.Ternary;
    }

    static ExprContext newAssignmentCtx(JTypeMirror targetType) {
        return new RegularCtx(targetType, CtxKind.Assignment);
    }

    static ExprContext newNonPolyContext(JTypeMirror targetType) {
        return new RegularCtx(targetType, CtxKind.OtherNonPoly);
    }

    static ExprContext newStringCtx(TypeSystem ts) {
        JClassType stringType = (JClassType) TypesFromReflection.fromReflect(String.class, ts);
        return new RegularCtx(stringType, CtxKind.String);
    }

    static ExprContext newNumericContext(JTypeMirror targetType) {
        if (targetType.isPrimitive()) {
            assert targetType.isNumeric() : targetType;
            return new RegularCtx(targetType, CtxKind.Numeric);
        }
        return RegularCtx.NO_CTX; // error
    }

    static ExprContext newCastCtx(JTypeMirror targetType) {
        return new RegularCtx(targetType, CtxKind.Cast);
    }

    static ExprContext newSuperCtorCtx(JTypeMirror superclassType) {
        return new RegularCtx(superclassType, CtxKind.Assignment);
    }

    static ExprContext newStandaloneTernaryCtx(JTypeMirror ternaryType) {
        return new RegularCtx(ternaryType, CtxKind.Ternary);
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

        @Override
        public String toString() {
            return "InvocCtx{arg=" + arg + ", node=" + node + '}';
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
        Assignment,

        /**
         * Cast context. Lambdas and method refs can use them as a
         * target type, but no other expressions. Cast contexts do not
         * flow through ternary/switch branches.
         */
        Cast,

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
        Numeric,

        /**
         * String contexts, which convert the operand to a string using {@link String#valueOf(Object)},
         * or the equivalent for a primitive type. They accept operands of any type.
         * This is the context for the operands of a string concatenation expression,
         * and for the message of an assert statement.
         */
        String,

        /** Kind for a standalone ternary (both branches are then in this context). */
        Ternary,

        /** Kind for a missing context ({@link RegularCtx#NO_CTX}). */
        Missing,

        /**
         * Other kinds of situation that have a target type for conversions,
         * but not for poly expressions. These do not flow through ternary branches.
         * These include:
         * <ul>
         * <li>
         * <li>Boolean contexts, which unbox their operand to a boolean.
         * They accept operands of type boolean or Boolean. This is the
         * context for e.g. the condition of an {@code if} statement, an
         * assert statement, etc.
         * </ul>
         */
        OtherNonPoly,
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
            return kind == CtxKind.Assignment || kind == CtxKind.Cast && lambdaOrMethodRef;
        }

        /**
         * Returns the target type bestowed by this context ON A POLY EXPRESSION.
         *
         * @param lambdaOrMethodRef Whether the poly to be considered is a
         *                          lambda or method ref. In this case, cast
         *                          contexts can give a target type.
         */
        @Nullable JTypeMirror getPolyTargetType(boolean lambdaOrMethodRef) {
            if (!canGiveContextToPoly(lambdaOrMethodRef)) {
                return null;
            }
            return targetType;
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
