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
import net.sourceforge.pmd.lang.java.ast.MethodUsage;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.TypingContext;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * Adapter class to manipulate expressions. The framework
 * ideally keeps focus on types and doesn't have a dependency
 * on the AST. Only the impl package can have such dependencies.
 */
public interface ExprMirror {

    /**
     * Returns a node which is used as a location to report messages.
     * Do not use this any other way.
     */
    JavaNode getLocation();

    /**
     * Return the text of the location node. May be overridden if this
     * mirror is fake (eg, using a lambda node but presenting a method ref,
     * or using a method ref node but presenting an invocation).
     * Use this only to log messages and for debugging.
     */
    default CharSequence getLocationText() {
        return getLocation().getText();
    }


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
     * For a standalone expr, finish type inference by computing properties
     * that are guarded by the type res lock. For instance for a standalone
     * ctor call, the standalone type is trivially known (it's the type node).
     * But we still need to do overload resolution.
     */
    default void finishStandaloneInference(@NonNull JTypeMirror standaloneType) {
        // do nothing
    }


    /**
     * Set the type of the underlying ast node. Used when we need
     * to find out the type of a poly to infer the type of another,
     * that way, we don't repeat computation.
     */
    void setInferredType(JTypeMirror mirror);

    /** Return the value set in the last call to {@link #setInferredType(JTypeMirror)}. */
    @Nullable JTypeMirror getInferredType();

    /**
     * Returns typing information for the lambdas parameters in scope
     * in this expression and its subexpressions. When overload resolution
     * involves lambdas, we might have to try several target types for each
     * lambda. Each of those may give a different type to the lambda parameters,
     * and hence, to every expression in the lambda body. These "tentative"
     * typing are kept in the {@link TypingContext} object and only
     * committed to the AST for the overload that is selected in the end.
     */
    TypingContext getTypingContext();

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
    default @NonNull TypeSpecies getStandaloneSpecies() {
        JTypeMirror std = getStandaloneType();
        return std == null ? UNKNOWN : getSpecies(std);
    }


    /**
     * Returns true if this mirror and its subexpressions are equivalent
     * to the underlying AST node. This is only relevant when making mirrors
     * that are not exactly equal to the AST node (eg, omitting explicit type arguments),
     * in order to check if the transformation does not change the meaning of the program.
     * It verifies that method and constructor calls are overload-selected
     * to the same compile-time declaration, and that nested lambdas
     * have the same type as in the AST.
     *
     * <p>This mirror's state, as filled-in during type resolution by
     * {@link Infer} using the various setters of {@link ExprMirror}
     * interfaces, is compared to the AST's corresponding state. Consequently,
     * if this state is missing (meaning, that no overload resolution
     * has been run using this mirror), the analysis cannot be performed
     * and an exception is thrown.
     *
     * @throws IllegalStateException If this mirror has not been used for overload resolution
     */
    boolean isEquivalentToUnderlyingAst();


    /**
     * Ground this expression and any expressions that might have been
     * assigned a type/ other data during type inference of this node.
     * This is called when inference in a parent expression failed, to
     * clean up partial data like type inference.
     *
     * <p>This is only called if the invocation fails, not when testing
     * for applicability. The reason is that this is really only relevant
     * for lambdas (to reset the type of their parameters), and those are
     * not relevant to applicability.
     */
    default void groundTree() {
        setInferredType(ensureNoTypeVariables(getInferredType()));
    }

    static JTypeMirror ensureNoTypeVariables(JTypeMirror ty) {
        if (ty == null) {
            return null;
        }
        return ty.subst(InferenceContext.finalGroundSubst());
    }

    static JMethodSig ensureNoTypeVariables(JMethodSig ty) {
        if (ty == null) {
            return null;
        }
        return ty.subst(InferenceContext.finalGroundSubst());
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

        /**
         * Record on the AST node that is is a standalone expression.
         * This accounts for special cases in the spec which are made
         * for numeric and boolean conditional expressions. For those
         * types of standalone exprs, the branches may have an additional
         * implicit unboxing/widening conversion, that does not depend
         * on the usual target type (the context of the ternary itself),
         * but just on the other branch.
         */
        default void setStandalone() {
            // do nothing by default
        }


        @Override
        default boolean isEquivalentToUnderlyingAst() {
            return branchesMatch(ExprMirror::isEquivalentToUnderlyingAst);
        }
    }

    /**
     * Mirror of some expression that targets a functional interface type:
     * lambda or method reference.
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
         * {@code java.util.function.Function<java.lang.String, java.lang.Boolean>.apply(java.lang.String) ->
         * java.lang.Boolean}
         *
         * <p>May be null if we're resetting some partial data.
         */
        void setFunctionalMethod(@Nullable JMethodSig methodType);

        /**
         * If the matching between this expr and its target type failed,
         * finish the inference by setting the data to UNKNOWN, or likely
         * values. This is used as a fallback.
         *
         * @param targetType Target type for the expression, null if there is none
         */
        void finishFailedInference(@Nullable JTypeMirror targetType);
    }

    /**
     * Common interface for {@link InvocationMirror} and {@link MethodRefMirror},
     * both of which wrap nods that implement {@link MethodUsage}.
     */
    interface MethodUsageMirror extends PolyExprMirror {

        /**
         * Set the compile-time declaration that was resolved for this method usage.
         */
        void setCompileTimeDecl(InvocationMirror.MethodCtDecl methodType);

        /**
         * Return the type in which the search for accessible methods start.
         * For method references it is the type of the LHS and is specified by
         * the JLS. For method invocations it is the type of the receiver, or
         * the type of the enclosing type. For constructor invocations this
         * is not defined and will return null.
         */
        @Nullable JTypeMirror getTypeToSearch();
    }

    /**
     * Mirror of a method reference expression.
     */
    interface MethodRefMirror extends FunctionalExprMirror, MethodUsageMirror {

        /** True if this references a ctor. */
        boolean isConstructorRef();


        /**
         * Returns the type to search as defined by the first section of
         * <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-15.html#jls-15.13.1">JLS§15.13.1</a>
         * , except it may also return an array type (the jls makes an exception for it,
         * while we don't).
         */
        @Override
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

        default boolean isLhsAType() {
            return getLhsIfType() != null;
        }

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
        @Override
        void setCompileTimeDecl(InvocationMirror.MethodCtDecl methodType);


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
         * is implicitly typed, then returns null. If some parameters
         * have a var type, returns {@link TypeSystem#UNKNOWN} for those.
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

        /**
         * Set the currently considered type of the parameters.
         * This may change depending on which target type we are currently
         * considering. The type of parameters (and therefore the typing context)
         * may influence the type of the return values of the lambda.
         *
         * @param formalParameters formal parameter types of the lambda
         */
        void updateTypingContext(List<? extends JTypeMirror> formalParameters);
    }

    /**
     * Adapter over a method or constructor invocation expression.
     */
    interface InvocationMirror extends PolyExprMirror, MethodUsageMirror {


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


        /** Return the size of the argument list. */
        int getArgumentCount();


        /**
         * {@inheritDoc}
         *
         * @implSpec Should cache this value and return it when {@link #getCtDecl()}
         * is called.
         */
        @Override
        void setCompileTimeDecl(MethodCtDecl methodType);


        /**
         * Returns the method type set with {@link #setCompileTimeDecl(MethodCtDecl)}
         * or null if that method was never called. This is used to perform
         * overload resolution exactly once per call site.
         */
        @Nullable MethodCtDecl getCtDecl();

        @Override
        default @Nullable JTypeMirror getTypeToSearch() {
            return getReceiverType();
        }


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
            private final OptionalBool needsUncheckedConversion;
            private final boolean failed;
            private final @Nullable MethodUsageMirror expr;

            MethodCtDecl(JMethodSig methodType,
                         MethodResolutionPhase resolvePhase,
                         boolean canSkipInvocation,
                         OptionalBool needsUncheckedConversion,
                         boolean failed,
                         @Nullable MethodUsageMirror expr) {
                this.methodType = methodType;
                this.resolvePhase = resolvePhase;
                this.canSkipInvocation = canSkipInvocation;
                this.needsUncheckedConversion = needsUncheckedConversion;
                this.failed = failed;
                this.expr = expr;
            }

            // package-private:

            public MethodCtDecl withMethod(JMethodSig method) {
                return withMethod(method, failed);
            }

            MethodCtDecl withMethod(JMethodSig method, boolean failed) {
                return new MethodCtDecl(method, resolvePhase, canSkipInvocation, needsUncheckedConversion, failed, expr);
            }

            public MethodCtDecl withExpr(MethodUsageMirror expr) {
                return new MethodCtDecl(methodType, resolvePhase, canSkipInvocation, needsUncheckedConversion, failed, expr);
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
                return new MethodCtDecl(ts.UNRESOLVED_METHOD, STRICT, true, OptionalBool.UNKNOWN, true, null);
            }

            // public:


            @Override
            public JMethodSig getMethodType() {
                return methodType;
            }

            @Override
            public boolean needsUncheckedConversion() {
                return needsUncheckedConversion.isTrue();
            }

            @Override
            public boolean isVarargsCall() {
                return resolvePhase.requiresVarargs();
            }

            @Override
            public boolean isFailed() {
                return failed;
            }

            @Override
            public String toString() {
                return "CtDecl[phase=" + resolvePhase + ", method=" + methodType + ']';
            }

            @Override
            public @Nullable JTypeMirror getTypeToSearch() {
                return expr != null ? expr.getTypeToSearch() : null;
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
         *
         * <p>This default implementation uses {@link #getAccessibleCandidates(JTypeMirror)},
         * which should be implemented instead.
         */
        @Override
        default Iterable<JMethodSig> getAccessibleCandidates() {
            return getAccessibleCandidates(getNewType());
        }

        /**
         * Returns the accessible candidates for this node, as if {@link #getNewType()}
         * returned the type passed as parameter. Since candidates depend on the
         * new type, this allows us to write simple "spy" wrappers to redo an invocation
         * in different conditions (ie, pretending the newtype is the parameter)
         *
         * @param newType Assumed value of {@link #getNewType()}
         */
        Iterable<JMethodSig> getAccessibleCandidates(JTypeMirror newType);


        /** Must return {@link JConstructorSymbol#CTOR_NAME}. */
        @Override
        default String getName() {
            return JConstructorSymbol.CTOR_NAME;
        }


    }
}
