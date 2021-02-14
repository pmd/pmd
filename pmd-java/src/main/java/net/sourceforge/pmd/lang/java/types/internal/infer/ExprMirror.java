/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.TypeSpecies.UNKNOWN;
import static net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.TypeSpecies.getSpecies;
import static net.sourceforge.pmd.lang.java.types.internal.infer.MethodResolutionPhase.STRICT;

import java.util.List;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 * Adapter class to manipulate expressions. The framework
 * ideally keeps focus on types and doesn't have a dependency
 * on the AST. Only the impl package can have such dependencies.
 */
public interface ExprMirror {

    JavaNode getLocation();


    /**
     * If this expression is of a standalone form, returns the type of
     * the expression. Otherwise returns null.
     *
     * <p>Note that standalone types can directly be set on the type
     * node.
     *
     * @return The type of the expression if it is standalone
     */
    @Nullable JTypeMirror getStandaloneType();


    /**
     * Returns the species that this expression produces. The species
     * may be known even if the expr is not standalone. For example a
     * diamond constructor call is not standalone, but its species is
     * obviously REFERENCE.
     *
     * <p>This is used for specificity tests for lambdas. They use species
     * because invocation needs to be done exactly once, and the actual
     * type of the expression may differ depending on the selected overload.
     * Eg given the signatures {@code <T>foo(Supplier<T>)} and {@code foo(Runnable)},
     * the expression {@code foo(() -> new List<>())} must select the supplier
     * overload, even before the invocation type of {@code List<>} is known.
     * The overload selection compares the expected species of both function
     * types (REFERENCE for Supplier, VOID for Runnable), and determines that
     * the supplier is more appropriate.
     */
    default TypeSpecies getStandaloneSpecies() {
        JTypeMirror std = getStandaloneType();
        return std == null ? UNKNOWN : getSpecies(std);
    }

    /** A general category of types. */
    enum TypeSpecies {
        PRIMITIVE,
        REFERENCE,
        VOID,
        UNKNOWN;


        public static TypeSpecies getSpecies(JTypeMirror t) {
            if (t.isPrimitive()) {
                return PRIMITIVE;
            } else if (t.isVoid()) {
                return VOID;
            } else if (TypeOps.isSpecialUnresolved(t)) {
                return UNKNOWN;
            }
            return REFERENCE;
        }
    }


    /**
     * Set the type of the underlying ast node. Used when we need
     * to find out the type of a poly to infer the type of another,
     * that way, we don't repeat computation.
     */
    void setInferredType(JTypeMirror mirror);


    interface PolyExprMirror extends ExprMirror {


        /**
         * Returns the class declaration wherein this invocation occurs.
         * Returns null if it's unresolved.
         */
        @NonNull JClassType getEnclosingType();


        @Override
        default @Nullable JTypeMirror getStandaloneType() {
            return null;
        }


        /**
         * If inference failed to determine the type of this node, returns
         * a fallback for it. This should not query the context of the expression,
         * or nodes whose type is unstable because it may be being inferred.
         *
         * <p>If no fallback should be used, returns null.
         */
        default @Nullable JTypeMirror unresolvedType() {
            return null;
        }
    }



    /** Mirrors a conditional or switch expression. */
    interface BranchingMirror extends PolyExprMirror {

        /**
         * Returns true if every result expression matches the given
         * predicate.
         */
        boolean branchesMatch(Predicate<? super ExprMirror> condition);

    }

    /**
     * Mirror of some expression that targets a functional interface type:
     * lambda or method reference.
     *
     * todo possibly, introduce the same kind of interface in the AST
     */
    interface FunctionalExprMirror extends PolyExprMirror {

        /**
         * For a method ref or lambda, this is the type of the functional interface.
         * E.g. in {@code stringStream.map(String::isEmpty)}, this is
         * {@code java.util.function.Function<java.lang.String, java.lang.Boolean>}
         *
         * <p>May be null if we're resetting some partial data.
         */
        @Override
        void setInferredType(@Nullable JTypeMirror mirror);


        /**
         * This is the method that is overridden in getInferredType.
         * E.g. in {@code stringStream.map(String::isEmpty)}, this is
         * {@code java.util.function.Function<java.lang.String, java.lang.Boolean>.apply(java.lang.String) -> java.lang.Boolean}
         *
         * <p>May be null if we're resetting some partial data.
         */
        void setFunctionalMethod(@Nullable JMethodSig methodType);

    }

    /**
     * Mirror of a method reference expression.
     */
    interface MethodRefMirror extends FunctionalExprMirror {

        /** True if this references a ctor. */
        boolean isConstructorRef();


        /**
         * Returns the type to search as defined by the first section of
         * <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-15.html#jls-15.13.1">JLS§15.13.1</a>
         * , except it may also return an array type (the jls makes an exception for it,
         * while we don't).
         */
        JTypeMirror getTypeToSearch();


        /**
         * Returns the type of the left hand-side, if it is not an expression.
         * Note that the following qualifier super forms are considered "expressions",
         * that have a context-dependent type (depends on the type of the {@code this} expr):
         * <pre>
         * super :: [TypeArguments] Identifier
         * TypeName.super :: [TypeArguments] Identifier
         * </pre>
         */
        @Nullable
        JTypeMirror getLhsIfType();


        /**
         * Returns the name of the invoked method, or {@link JConstructorSymbol#CTOR_NAME}
         * if this is a constructor reference.
         */
        String getMethodName();


        /** Returns the explicit type arguments (the ones to the right of the "::"). */
        @NonNull List<JTypeMirror> getExplicitTypeArguments();

        /**
         * This is the method that is referenced.
         * E.g. in {@code stringStream.map(String::isEmpty)}, this is
         * {@code java.lang.String.isEmpty() -> boolean}
         */
        void setCompileTimeDecl(JMethodSig methodType);


        /**
         * UNRESOLVED_METHOD if not yet computed, null if computed but
         * inexact, otherwise the real method.
         */
        @Nullable
        JMethodSig getCachedExactMethod();


        void setCachedExactMethod(@Nullable JMethodSig sig);

    }

    /** Mirrors a lambda expression. */
    interface LambdaExprMirror extends FunctionalExprMirror {

        /**
         * Returns the types of the explicit parameters. If the lambda
         * is implicitly typed, then returns null.
         *
         * <p>Note that a degenerate case of explicitly typed lambda
         * expression is a lambda with zero formal parameters.
         */
        @Nullable List<JTypeMirror> getExplicitParameterTypes();

        /**
         * See {@link #getExplicitParameterTypes()}.
         */
        default boolean isExplicitlyTyped() {
            return getExplicitParameterTypes() != null;
        }

        /**
         * Return the number of parameters of the lambda, regardless of
         * whether it's explicitly typed or not.
         */
        int getParamCount();


        /**
         * Returns all the expressions that appear in {@code return}
         * statements within the lambda. If this is an expression-bodied
         * lambda, returns the expression.
         */
        Iterable<ExprMirror> getResultExpressions();


        /**
         * Returns true if the body is value-compatible {@literal (JLS§15.27.2)}.
         * <blockquote>
         *     A block lambda body is value-compatible if it cannot complete
         *     normally (§14.21) and every return statement in the block
         *     has the form return Expression;.
         * </blockquote>
         */
        boolean isValueCompatible();

        /**
         * Returns true if the body is void-compatible {@literal (JLS§15.27.2)}.
         * <blockquote>
         *     A block lambda body is void-compatible if every return
         *     statement in the block has the form return;.
         * </blockquote>
         */
        boolean isVoidCompatible();
    }

    /**
     * Adapter over a method or constructor invocation expression.
     */
    interface InvocationMirror extends PolyExprMirror {

        /**
         * Enumerates *accessible* method (or ctor) signatures with
         * *the same name* as this invocation. Name and accessibility
         * will not be checked later.
         *
         * The details on how to determine this are here:
         *
         * https://docs.oracle.com/javase/specs/jls/se9/html/jls-15.html#jls-15.12.1
         */
        Iterable<JMethodSig> getAccessibleCandidates();


        /**
         * Returns the erased receiver type. This is only used to adapt the
         * {@code Object::getClass} method, other types of invocations don't
         * need to implement this.
         */
        default @Nullable JTypeMirror getErasedReceiverType() {
            return null;
        }


        /**
         * Returns the erased receiver type. This is only used for method
         * invocations.
         */
        @Nullable JTypeMirror getReceiverType();


        /**
         * Returns the explicit type arguments, eg in {@code Arrays.<String>asList("q")},
         * or {@code new <String> Foo("q")}. If none are mentioned, returns an empty list.
         */
        List<JTypeMirror> getExplicitTypeArguments();


        /**
         * @throws IndexOutOfBoundsException If there's no explicit type argument at the given index
         */
        JavaNode getExplicitTargLoc(int i);


        /**
         * Returns the name of the invoked method. If this is a
         * constructor call, returns {@link JConstructorSymbol#CTOR_NAME}.
         */
        String getName();


        /** Returns the expressions corresponding to the arguments of the call. */
        List<ExprMirror> getArgumentExpressions();


        int getArgumentCount();


        void setMethodType(MethodCtDecl methodType);


        /**
         * Returns the method type set with {@link #setMethodType(MethodCtDecl)}
         * or null if that method was never called. This is used to perform
         * overload resolution exactly once per call site.
         */
        @Nullable
        MethodCtDecl getMethodType();


        /**
         * Information about the overload-resolution for a specific method.
         */
        class MethodCtDecl implements OverloadSelectionResult {
            // note this data is gathered by the MethodCallSite during
            // applicability inference, stashed in this object, and
            // restored when we do invocation.

            private final JMethodSig methodType;
            private final MethodResolutionPhase resolvePhase;
            private final boolean canSkipInvocation;
            private final boolean needsUncheckedConversion;
            private final boolean failed;

            MethodCtDecl(JMethodSig methodType,
                         MethodResolutionPhase resolvePhase,
                         boolean canSkipInvocation,
                         boolean needsUncheckedConversion,
                         boolean failed) {
                this.methodType = methodType;
                this.resolvePhase = resolvePhase;
                this.canSkipInvocation = canSkipInvocation;
                this.needsUncheckedConversion = needsUncheckedConversion;
                this.failed = failed;
            }

            // package-private:

            MethodCtDecl withMethod(JMethodSig method) {
                return withMethod(method, failed);
            }

            MethodCtDecl withMethod(JMethodSig method, boolean failed) {
                return new MethodCtDecl(method, resolvePhase, canSkipInvocation, needsUncheckedConversion, failed);
            }

            MethodCtDecl asFailed() {
                return withMethod(methodType, true);
            }

            boolean canSkipInvocation() {
                return canSkipInvocation;
            }

            MethodResolutionPhase getResolvePhase() {
                return resolvePhase;
            }

            static MethodCtDecl unresolved(TypeSystem ts) {
                return new MethodCtDecl(ts.UNRESOLVED_METHOD, STRICT, true, false, true);
            }

            // public:


            @Override
            public JMethodSig getMethodType() {
                return methodType;
            }

            @Override
            public boolean needsUncheckedConversion() {
                return needsUncheckedConversion;
            }

            @Override
            public boolean isVarargsCall() {
                return resolvePhase.requiresVarargs();
            }

            @Override
            public JTypeMirror ithFormalParam(int i) {
                return resolvePhase.ithFormal(getMethodType().getFormalParameters(), i);
            }

            @Override
            public boolean isFailed() {
                return failed;
            }


            @Override
            public String toString() {
                return "CtDecl[phase=" + resolvePhase + ", method=" + methodType + ']';
            }

        }
    }

    /**
     * An invocation mirror reflecting a constructor invocation expression.
     */
    interface CtorInvocationMirror extends InvocationMirror {

        /**
         * Return the type name being instantiated. If the constructor call
         * is a diamond invocation (or no type args), returns the generic type declaration.
         * Otherwise returns the parameterised type. If the call declares
         * an anonymous class, then this does *not* return the anonymous
         * type, but its explicit supertype.
         *
         * <ul>
         *  <li>e.g. for {@code new ArrayList<>()}, returns {@code ArrayList<T>}.
         *  <li>e.g. for {@code new ArrayList()}, returns {@code ArrayList}.
         *  <li>e.g. for {@code new ArrayList<String>()}, returns {@code ArrayList<String>}.
         *  <li>e.g. for {@code new Runnable() {}} (anonymous), returns {@code Runnable}.
         * </ul>
         *
         * <p>Note that this returns a {@link JClassType} in valid code.
         * Other return values may be eg {@link TypeSystem#UNKNOWN}, or
         * a {@link JTypeVar}, but indicate malformed code.
         */
        @NonNull JTypeMirror getNewType();

        /**
         * True if this creates an anonymous class. Since java 9 those
         * can also be diamond-inferred.
         */
        boolean isAnonymous();

        /**
         * Return true if this is a diamond constructor call. In that
         * case the type parameters of the created instance must be inferred.
         * Returns false if the constructor call mentions no type arguments.
         * <ul>
         *  <li>e.g. for {@code new ArrayList<>()}, returns true.
         *  <li>e.g. for {@code new ArrayList()}, returns false.
         *  <li>e.g. for {@code new ArrayList<String>()}, returns false.
         * </ul>
         */
        boolean isDiamond();


        /**
         * {@inheritDoc}
         *
         * <p>Returns the constructor of the {@link #getNewType()}. If
         * this is an anonymous class declaration implementing an interface,
         * then returns the constructors of class {@link Object}.
         */
        @Override
        Iterable<JMethodSig> getAccessibleCandidates();


        /** Must return {@link JConstructorSymbol#CTOR_NAME}. */
        @Override
        default String getName() {
            return JConstructorSymbol.CTOR_NAME;
        }


    }
}
