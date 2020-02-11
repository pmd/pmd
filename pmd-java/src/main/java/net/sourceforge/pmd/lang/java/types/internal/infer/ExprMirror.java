/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static net.sourceforge.pmd.lang.java.types.internal.infer.MethodResolutionPhase.STRICT;

import java.util.List;
import java.util.stream.Stream;

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
        @Nullable
        default JTypeMirror getStandaloneType() {
            return null;
        }


    }

    interface MethodRefMirror extends PolyExprMirror {

        boolean isConstructorRef();


        /**
         * Returns the type to search as defined by the first section of
         *
         * https://docs.oracle.com/javase/specs/jls/se9/html/jls-15.html#jls-15.13.1
         *
         * , except it may also return an array type.
         */
        JTypeMirror getTypeToSearch();


        /**
         * Returns the type of the left hand-side, if it is not an expression.
         */
        @Nullable
        JTypeMirror getLhsIfType();


        /**
         * Returns the name of the invoked method, or {@link JConstructorSymbol#CTOR_NAME}
         * if this is a constructor reference.
         */
        String getMethodName();


        /** Returns the explicit type arguments (the ones to the right of the "::"). */
        List<JTypeMirror> getExplicitTypeArguments();


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
         * Stream of the branches of this expr. Returns a
         * new stream each time.
         */
        Stream<ExprMirror> getBranches();

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


        boolean isValueCompatible();


        boolean isVoidCompatible();


        void setFunctionalMethod(JMethodSig methodType);
    }

    /**
     * Adapter representing a method invocation expression for the purposes
     * of determining applicability.
     */
    interface InvocationMirror extends PolyExprMirror {

        /**
         * Returns a stream of method types that contain the visible
         * methods (or constructors) with *the same name* as this
         * invocation (will not be checked later).
         *
         * <p>Accessibility of the method at the call site will be
         * checked later.
         *
         * The details on how to determine this are here:
         *
         * https://docs.oracle.com/javase/specs/jls/se9/html/jls-15.html#jls-15.12.1
         */
        List<JMethodSig> getVisibleCandidates();


        /**
         * Returns the erased receiver type. This is only used to adapt the
         * {@link #getClass()} method, other types of invocations don't
         * need to implement this.
         */
        @Nullable
        default JTypeMirror getErasedReceiverType() {
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


        /** transient data used during the analysis */
        class MethodCtDecl {
            private final JMethodSig methodType;
            private final MethodResolutionPhase resolvePhase;

            MethodCtDecl(JMethodSig methodType, MethodResolutionPhase resolvePhase) {
                this.methodType = methodType;
                this.resolvePhase = resolvePhase;
            }

            public JMethodSig getMethodType() {
                return methodType;
            }

            public boolean phaseRequiresVarargs() {
                return resolvePhase.requiresVarargs();
            }

            MethodResolutionPhase getResolvePhase() {
                return resolvePhase;
            }

            @Override
            public String toString() {
                return "CtDecl[phase=" + resolvePhase +
                    ", method=" + methodType +
                    ']';
            }

            public static MethodCtDecl unresolved(TypeSystem ts) {
                return new MethodCtDecl(ts.UNRESOLVED_METHOD, STRICT);
            }
        }
    }

    /**
     * An invocation mirror reflecting a constructor invocation expression.
     */
    interface CtorInvocationMirror extends InvocationMirror {

        /**
         * Return the type name being instantiated. If the constructor call
         * is a diamond invocation (or no type args), returns the raw type.
         * Otherwise returns the parameterised type. If the call declares
         * an anonymous class, then this does *not* return the anonymous
         * type, but its supertype.
         *
         * <ul>
         *  <li>e.g. for {@code new ArrayList<>()}, returns {@code ArrayList}.
         *  <li>e.g. for {@code new ArrayList()}, returns {@code ArrayList}.
         *  <li>e.g. for {@code new ArrayList<String>()}, returns {@code ArrayList<String>}.
         *  <li>e.g. for {@code new Runnable() {}} (anonymous), returns {@code Runnable}.
         * </ul>
         */
        JClassType getNewType();


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
        List<JMethodSig> getVisibleCandidates();


        /** Must return {@link JConstructorSymbol#CTOR_NAME}. */
        @Override
        default String getName() {
            return JConstructorSymbol.CTOR_NAME;
        }


    }
}
