/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

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
     * @return The type of the expression if it is standalone
     */
    @Nullable
    JTypeMirror getStandaloneType();


    interface PolyExprMirror extends ExprMirror {

        /**
         * Set the type of the underlying ast node. Used when we need
         * to find out the type of a poly to infer the type of another,
         * that way, we don't repeat computation.
         */
        void setInferredType(JTypeMirror mirror);


        /**
         * Returns the class declaration wherein this invocation occurs.
         * Returns null if it's unresolved.
         */
        @NonNull
        JClassType getEnclosingType();


        @Override
        default @Nullable JTypeMirror getStandaloneType() {
            return null;
        }


    }

    /**
     * Mirror of a method reference expression.
     */
    interface MethodRefMirror extends PolyExprMirror {

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
         * For a method ref, this is the type of the functional interface.
         * E.g. in {@code stringStream.map(String::isEmpty)}, this is
         * {@code java.util.function.Function<java.lang.String, java.lang.Boolean>}
         */
        @Override
        void setInferredType(JTypeMirror mirror);


        /**
         * For a method ref, this is the method that is overridden in getInferredType.
         * E.g. in {@code stringStream.map(String::isEmpty)}, this is
         * {@code java.util.function.Function<java.lang.String, java.lang.Boolean>.apply(java.lang.String) -> java.lang.Boolean}
         */
        void setFunctionalMethod(JMethodSig methodType);


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

    /** Mirrors a conditional or switch expression. */
    interface BranchingMirror extends PolyExprMirror {

        /**
         * Returns true if every result expression matches the given
         * predicate.
         */
        boolean branchesMatch(Predicate<? super ExprMirror> condition);

    }

    /** Mirrors a lambda expression. */
    interface LambdaExprMirror extends PolyExprMirror {

        /**
         * Returns the types of the explicit parameters. If the lambda
         * is implicitly typed, then return an empty list.
         */
        List<JTypeMirror> getExplicitParameterTypes();


        /**
         * Return the number of parameters of the lambda, regardless of
         * whether it's explicitly typed or not.
         */
        int getParamCount();


        default boolean isExplicitlyTyped() {
            return getParamCount() != 0 && getParamCount() == getExplicitParameterTypes().size();
        }


        /**
         * Returns all the expressions that appear in {@code return}
         * statements within the lambda. If this is an expression-bodied
         * lambda, returns the expression.
         */
        Iterable<ExprMirror> getResultExpressions();


        /**
         * Returns true if the body is value-compatible, ie it is either
         * an expression, or a block whose return statements return an
         * expression.
         */
        boolean isValueCompatible();

        /**
         * Returns true if the body is value-compatible, ie it is either
         * an expression which "may" have type void (eg, not {@code a + 1}),
         * or a block whose return statements return no expression.
         */
        boolean isVoidCompatible();


        void setFunctionalMethod(JMethodSig methodType);
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
        default @Nullable JTypeMirror getReceiverType() {
            return null;
        }


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


        /** Result of an inference run. */
        class MethodCtDecl {
            // note this data is gathered by the MethodCallSite, stashed
            // in this object, and restored when we play invocation.

            private final JMethodSig methodType;
            private final MethodResolutionPhase resolvePhase;
            private final boolean argsAreAllRelevant;
            private final boolean needsUncheckedConversion;
            private final boolean failed;

            MethodCtDecl(JMethodSig methodType,
                         MethodResolutionPhase resolvePhase,
                         boolean argsAreAllRelevant,
                         boolean needsUncheckedConversion,
                         boolean failed) {
                this.methodType = methodType;
                this.resolvePhase = resolvePhase;
                this.argsAreAllRelevant = argsAreAllRelevant;
                this.needsUncheckedConversion = needsUncheckedConversion;
                this.failed = failed;
            }

            // package-private:

            MethodCtDecl withMethod(JMethodSig method) {
                return withMethod(method, false);
            }

            MethodCtDecl withMethod(JMethodSig method, boolean failed) {
                return new MethodCtDecl(method, resolvePhase, argsAreAllRelevant, needsUncheckedConversion, failed);
            }

            boolean areAllArgsRelevant() {
                return argsAreAllRelevant;
            }

            MethodResolutionPhase getResolvePhase() {
                return resolvePhase;
            }

            static MethodCtDecl unresolved(TypeSystem ts, boolean isFailed) {
                return new MethodCtDecl(ts.UNRESOLVED_METHOD, STRICT, true, false, isFailed);
            }

            // public:

            /**
             * The result type. After an invocation phase, this is the
             * method type of the ctdecl, substituted with the instantiations
             * of the type parameters as inferred with context and such,
             * possibly adapted in some special cases ({@code getClass}).
             */
            public JMethodSig getMethodType() {
                return methodType;
            }

            /**
             * Whether the declaration needed unchecked conversion to be
             * applicable. In this case, the return type of the method is
             * erased.
             */
            public boolean needsUncheckedConversion() {
                return needsUncheckedConversion;
            }

            /**
             * Returns whether the overload resolution phase that selected
             * this overload was a varargs phase. In this case, the last
             * formal parameter of the method type should be interpreted
             * specially with-respect-to the argument expressions.
             */
            public boolean phaseRequiresVarargs() {
                return resolvePhase.requiresVarargs();
            }

            /**
             * Returns true if the invocation of this method failed. This
             * means, the presented method type is a fallback, whose type
             * parameters might not have been fully instantiated.
             */
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
         */
        JClassType getNewType();

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
