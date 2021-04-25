/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;


import static java.util.Arrays.asList;
import static net.sourceforge.pmd.internal.util.AssertionUtil.shouldNotReachHere;
import static net.sourceforge.pmd.lang.java.types.TypeConversion.isConvertibleUsingBoxing;
import static net.sourceforge.pmd.util.CollectionUtil.all;
import static net.sourceforge.pmd.util.CollectionUtil.map;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ExprContext.InvocCtx;
import net.sourceforge.pmd.lang.java.ast.ExprContext.RegularCtx;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.BranchingMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.FunctionalExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.Infer;
import net.sourceforge.pmd.lang.java.types.internal.infer.MethodCallSite;
import net.sourceforge.pmd.lang.java.types.internal.infer.PolySite;
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.JavaExprMirrors;

/**
 * Routines to handle context around poly expressions.
 */
final class PolyResolution {

    private final Infer infer;
    private final TypeSystem ts;
    private final JavaExprMirrors exprMirrors;

    PolyResolution(Infer infer) {
        this.infer = infer;
        this.ts = infer.getTypeSystem();
        this.exprMirrors = JavaExprMirrors.forTypeResolution(infer);
    }

    public Infer getInfer() {
        return infer;
    }

    private boolean isPreJava8() {
        return infer.isPreJava8();
    }

    JTypeMirror computePolyType(final TypeNode e) {
        if (!canBePoly(e)) {
            throw shouldNotReachHere("Unknown poly " + e);
        }

        ExprContext ctx = contextOf(e, false, true);

        if (ctx instanceof InvocCtx) {
            return polyTypeInvocationCtx(e, (InvocCtx) ctx);
        }

        return polyTypeOtherCtx(e, (RegularCtx) ctx);
    }

    private JTypeMirror polyTypeOtherCtx(TypeNode e, RegularCtx ctx) {
        // we have a context, that is not an invocation
        if (e instanceof InvocationNode) {
            // The original expr was an invocation, but we have
            // a context type (eg assignment context)
            JTypeMirror targetType = ctx.getPolyTargetType(false);

            return inferInvocation((InvocationNode) e, e, targetType);
        } else if (e instanceof ASTSwitchExpression || e instanceof ASTConditionalExpression) {
            // Those are standalone if possible, otherwise they take
            // the target type

            // in java 7 they are always standalone
            if (isPreJava8()) {
                // safe cast because ASTSwitchExpression doesn't exist pre java 13
                ASTConditionalExpression conditional = (ASTConditionalExpression) e;
                return computeStandaloneConditionalType(this.ts,
                                                        conditional.getThenBranch().getTypeMirror(),
                                                        conditional.getElseBranch().getTypeMirror());
            }

            // Note that this creates expr mirrors for all subexpressions,
            // and may trigger inference on them (which does not go through PolyResolution).
            // Because this process may fail if the conditional is not standalone,
            // the ctors for expr mirrors must have only trivial side-effects.
            // See comment in MethodRefMirrorImpl
            JTypeMirror target = ctx.getPolyTargetType(false);
            if (target != null) {
                // then it is a poly expression
                // only reference conditional expressions take the target type,
                // but the spec special-cases some forms of conditionals ("numeric" and "boolean")
                // The mirror recognizes these special cases
                BranchingMirror polyMirror = (BranchingMirror) exprMirrors.getPolyMirror((ASTExpression) e);
                JTypeMirror standaloneType = polyMirror.getStandaloneType();
                if (standaloneType != null) { // then it is one of those special cases
                    polyMirror.setStandalone(); // record this fact
                    return standaloneType;
                }
                // otherwise it's the target type
                return target;
            } else {
                // then it is standalone
                BranchingMirror branchingMirror = exprMirrors.getStandaloneBranchingMirror((ASTExpression) e);
                branchingMirror.setStandalone(); // record this fact

                JTypeMirror standalone = branchingMirror.getStandaloneType();
                if (standalone != null) {
                    return standalone;
                } else if (!ctx.canGiveContextToPoly(false)) {

                    // null standalone, force resolution anyway, because there is no context
                    // this is more general than ExprMirror#getStandaloneType, it's not a bug
                    if (e instanceof ASTSwitchExpression) {
                        // todo merge this fallback into SwitchMirror
                        //  That would be less easily testable that what's below...
                        List<JTypeMirror> branches = ((ASTSwitchExpression) e).getYieldExpressions().toList(TypeNode::getTypeMirror);
                        return computeStandaloneConditionalType(ts, branches);
                    } else {
                        throw AssertionUtil.shouldNotReachHere("ConditionalMirrorImpl returns non-null for conditionals");
                    }
                }
                return ts.ERROR;
            }
        } else if (e instanceof ASTMethodReference || e instanceof ASTLambdaExpression) {
            // these may use a cast as a target type
            JTypeMirror targetType = ctx.getPolyTargetType(true);
            return inferLambdaOrMref((ASTExpression) e, targetType);
        } else {
            throw shouldNotReachHere("Unknown poly " + e);
        }
    }

    private JTypeMirror inferLambdaOrMref(ASTExpression e, @Nullable JTypeMirror targetType) {
        FunctionalExprMirror mirror = exprMirrors.getFunctionalMirror(e);
        PolySite<FunctionalExprMirror> site = infer.newFunctionalSite(mirror, targetType);
        infer.inferFunctionalExprInUnambiguousContext(site);
        JTypeMirror result = InternalApiBridge.getTypeMirrorInternal(e);
        assert result != null : "Should be unknown";
        return result;
    }

    private @NonNull JTypeMirror polyTypeInvocationCtx(TypeNode e, InvocCtx ctx) {
        // an outer invocation ctx
        InvocationNode ctxInvoc = ctx.node;
        if (ctxInvoc instanceof ASTExpression) {
            // method call or regular constructor call
            // recurse, that will fetch the outer context
            ctxInvoc.getTypeMirror();
            return fetchCascaded(e);
        } else {
            return inferInvocation(ctxInvoc, e, null);
        }
    }

    /**
     * Given an invocation context (ctxNode), infer its most specific
     * method, which will set the type of the 'enclosed' poly expression.
     * The 'targetType' can influence the invocation type of the method
     * (not applicability).
     *
     * <p>Eg:
     *
     * <pre>{@code
     *
     *     <T> T coerce(int i) {
     *         return null;
     *     }
     *
     *     <K> Stream<K> streamK() {
     *         return Stream.of(1, 2).map(this::coerce);
     *     }
     *
     * }</pre>
     *
     * <p>There is only one applicable method for this::coerce so the
     * method reference is exact. However the type argument {@code <T>}
     * of coerce has no bound. The target type {@code Stream<K>} is
     * incorporated and we infer that coerce's type argument is {@code <K>}.
     *
     * <p>This is also why the following fails type inference:
     *
     * <pre>{@code
     *
     *     <K> List<K> streamK2() {
     *         // type checks when written this::<K>coerce
     *         return Stream.of(1, 2).map(this::coerce).collect(Collectors.toList());
     *     }
     *
     * }</pre>
     */
    private JTypeMirror inferInvocation(InvocationNode ctxNode, TypeNode actualResultTarget, @Nullable JTypeMirror targetType) {
        InvocationMirror mirror = exprMirrors.getInvocationMirror(ctxNode);
        MethodCallSite site = infer.newCallSite(mirror, targetType);
        infer.inferInvocationRecursively(site);
        // errors are on the call site if any

        return fetchCascaded(actualResultTarget);
    }

    /**
     * Fetch the resolved value when it was inferred as part of overload
     * resolution of an enclosing invocation context.
     */
    private @NonNull JTypeMirror fetchCascaded(TypeNode e) {
        // Some types are set as part of overload resolution
        // Conditional expressions also have their type set if they're
        // standalone
        JTypeMirror type = InternalApiBridge.getTypeMirrorInternal(e);
        if (type != null) {
            return type;
        }

        if (e.getParent().getParent() instanceof InvocationNode) {
            // invocation ctx
            InvocationNode parentInvoc = (InvocationNode) e.getParent().getParent();
            OverloadSelectionResult info = parentInvoc.getOverloadSelectionInfo();
            if (!info.isFailed()) {
                JTypeMirror targetT = info.ithFormalParam(e.getIndexInParent());
                if (e instanceof ASTLambdaExpression || e instanceof ASTMethodReference) {
                    // their types are not completely set
                    return inferLambdaOrMref((ASTExpression) e, targetT);
                }
                return targetT;
            }
        }

        // if we're here, we failed
        return fallbackIfCtxDidntSet(e);
    }

    /**
     * If resolution of the outer context failed, like if we call an unknown
     * method, we may still be able to derive the types of the arguments. We
     * treat them as if they occur as standalone expressions.
     * TODO would using error-type as a target type be better? could coerce
     * generic method params to error naturally
     */
    private @NonNull JTypeMirror fallbackIfCtxDidntSet(@Nullable TypeNode e) {
        // retry with no context
        return polyTypeOtherCtx(e, ExprContext.RegularCtx.NO_CTX);
        // infer.LOG.polyResolutionFailure(e);
    }

    /**
     * If true, the expression may depends on its target type. There may not
     * be a target type though - this is given by the {@link #contextOf(JavaNode, boolean, boolean)}.
     *
     * <p>If false, then the expression is standalone and its type is
     * only determined by the type of its subexpressions.
     */
    private static boolean canBePoly(TypeNode e) {
        return e instanceof ASTLambdaExpression
            || e instanceof ASTMethodReference
            || e instanceof ASTConditionalExpression
            || e instanceof ASTSwitchExpression
            || e instanceof InvocationNode;
    }

    // Some symbol is not resolved
    // go backwards from the context to get it.

    /**
     * Fallback for some standalone expressions, that may use some context
     * to set their type. This must not trigger any type inference process
     * that may need this expression. So if this expression is in an invocation
     * context, that context must not be called.
     */
    JTypeMirror getContextTypeForStandaloneFallback(ASTExpression e) {
        // The case mentioned by the doc is removed. We could be smarter
        // with how we retry failed invocation resolution, see history
        // of this comment

        @NonNull ExprContext ctx = contextOf(e, false, true);

        if (e.getParent() instanceof ASTSwitchLabel) {
            ASTSwitchLike switchLike = e.ancestors(ASTSwitchLike.class).firstOrThrow();
            // this may trigger some inference, which doesn't matter
            // as it is out of context
            return switchLike.getTestedExpression().getTypeMirror();
        }

        if (ctx instanceof RegularCtx) {
            JTypeMirror targetType = ((RegularCtx) ctx).getPolyTargetType(false);
            if (targetType != null) {
                return targetType;
            }
        }

        return ts.UNKNOWN;
    }

    /**
     * Not meant to be used by the main typeres paths, only for rules.
     */
    static ExprContext getConversionContextForExternalUse(ASTExpression e) {
        PolyResolution polyResolution = e.getRoot().getLazyTypeResolver().getPolyResolution();
        return polyResolution.contextOf(e, false, false);
    }

    private static @Nullable JTypeMirror returnTargetType(ASTReturnStatement context) {
        Node methodDecl =
            context.ancestors().first(
                it -> it instanceof ASTMethodDeclaration
                    || it instanceof ASTLambdaExpression
                    || it instanceof ASTAnyTypeDeclaration
            );

        if (methodDecl == null || methodDecl instanceof ASTAnyTypeDeclaration) {
            // in initializer, or constructor decl, return with expression is forbidden
            // (this is an error)
            return null;
        } else if (methodDecl instanceof ASTLambdaExpression) {
            // return within a lambda
            // "assignment context", deferred to lambda inference
            JMethodSig fun = ((ASTLambdaExpression) methodDecl).getFunctionalMethod();
            return fun == null ? null : fun.getReturnType();
        } else {
            @NonNull ASTType resultType = ((ASTMethodDeclaration) methodDecl).getResultTypeNode();
            return resultType instanceof ASTVoidType ? null // (this is an error)
                                                     : resultType.getTypeMirror();
        }
    }

    /**
     * Returns the node on which the type of the given node depends.
     * This addresses the fact that poly expressions depend on their
     * surrounding context for a target type. So when someone asks
     * for the type of a poly, we have to determine the type of the
     * context before we can determine the type of the poly.
     *
     * <p>The returned context may never be a conditional or switch,
     * those just forward an outer context to their branches.
     *
     * <p>If there is no context node, returns null.
     *
     * Examples:
     * <pre>
     *
     * new Bar<>(foo())  // contextOf(methodCall) = constructorCall
     *
     * this(foo())       // contextOf(methodCall) = explicitConstructorInvoc
     *
     * a = foo()         // contextOf(methodCall) = assignmentExpression
     * a = (Cast) foo()  // contextOf(methodCall) = castExpression
     * return foo();     // contextOf(methodCall) = returnStatement
     *
     * foo(a ? () -> b   // the context of each lambda, and of the conditional, is the methodCall
     *       : () -> c)
     *
     * foo();            // expression statement, no target type
     *
     * 1 + (a ? foo() : 2) //  contextOf(methodCall) = null
     *                     //  foo() here has no target type, because the enclosing conditional has none
     *
     *
     * </pre>
     */
    private @NonNull ExprContext contextOf(final JavaNode node, boolean onlyInvoc, boolean internalUse) {
        final JavaNode papa = node.getParent();
        if (papa instanceof ASTArgumentList) {
            // invocation context, return *the first method*
            // eg in
            // lhs = foo(bar(bog())),
            // contextOf(bog) = bar, contextOf(bar) = foo, contextOf(foo) = lhs
            // when asked the type of 'foo', we return 'bar'
            // we recurse indirectly and ask 'bar' for its type
            // it asks 'foo', which binds to 'lhs', and infers all types

            // we can't just recurse directly up, because then contextOf(bog) = lhs,
            // and that's not true (bog() is in an invocation context)
            final InvocationNode papi = (InvocationNode) papa.getParent();

            if (papi instanceof ASTExplicitConstructorInvocation || papi instanceof ASTEnumConstant) {
                return new InvocCtx(node.getIndexInParent(), papi);
            } else {
                if (isPreJava8()) {
                    // in java < 8 invocation contexts don't provide a target type
                    return RegularCtx.NO_CTX;
                }

                // Constructor or method call, maybe there's another context around
                // We want to fetch the outermost invocation node, but not further
                ExprContext outerCtx = contextOf(papi, /*onlyInvoc:*/true, internalUse);
                return outerCtx.canGiveContextToPoly(false)
                       ? outerCtx
                       // otherwise we're done, this is the outermost context
                       : new InvocCtx(node.getIndexInParent(), papi);
            }
        } else if (doesCascadesContext(papa, node, internalUse)) {
            // switch/conditional
            return contextOf(papa, onlyInvoc, internalUse);
        }

        if (onlyInvoc) {
            return RegularCtx.NO_CTX;
        }

        if (papa instanceof ASTArrayInitializer) {

            JTypeMirror target = TypeOps.getArrayComponent(((ASTArrayInitializer) papa).getTypeMirror());
            return ExprContext.newAssignmentCtx(target);

        } else if (papa instanceof ASTCastExpression) {

            JTypeMirror target = ((ASTCastExpression) papa).getCastType().getTypeMirror();
            return ExprContext.newCastCtx(target);

        } else if (papa instanceof ASTAssignmentExpression && node.getIndexInParent() == 1) { // second operand

            JTypeMirror target = ((ASTAssignmentExpression) papa).getLeftOperand().getTypeMirror();
            return ExprContext.newAssignmentCtx(target);

        } else if (papa instanceof ASTReturnStatement) {

            return ExprContext.newAssignmentCtx(returnTargetType((ASTReturnStatement) papa));

        } else if (papa instanceof ASTVariableDeclarator
            && !((ASTVariableDeclarator) papa).getVarId().isTypeInferred()) {

            return ExprContext.newAssignmentCtx(((ASTVariableDeclarator) papa).getVarId().getTypeMirror());

        } else if (papa instanceof ASTYieldStatement) {

            // break with value (switch expr)
            ASTSwitchExpression owner = ((ASTYieldStatement) papa).getYieldTarget();
            return contextOf(owner, false, internalUse);

        } else if (node instanceof ASTExplicitConstructorInvocation
            && ((ASTExplicitConstructorInvocation) node).isSuper()) {

            // the superclass type is taken as a target type for inference,
            // when the super ctor is generic/ the superclass is generic
            return ExprContext.newSuperCtorCtx(node.getEnclosingType().getTypeMirror().getSuperClass());

        } else if (papa instanceof ASTArrayAccess && node.getIndexInParent() == 1) {
            // array index
            return ExprContext.newNumericCtx(papa.getTypeSystem().INT);
        } else if (papa instanceof ASTConditionalExpression && node.getIndexInParent() != 0) {
            assert ((ASTConditionalExpression) papa).isStandalone() && !internalUse
                : "Expected standalone ternary, otherwise doesCascadeContext(..) would have returned true";
            return ExprContext.newStandaloneTernaryCtx(((ASTConditionalExpression) papa).getTypeMirror());
        } else {
            // stop recursion
            return RegularCtx.NO_CTX;
        }
    }


    /**
     * Identifies a node that can forward an invocation/assignment context
     * inward. If their parent has no context, then they don't either.
     */
    private boolean doesCascadesContext(JavaNode node, JavaNode child, boolean internalUse) {
        if (child.getParent() != node) {
            // means the "node" is a "stop recursion because no context" result in contextOf
            return false;
        } else if (isPreJava8()) {
            // in java < 8, context doesn't go flow through ternaries
            return false;
        } else if (!internalUse && node instanceof ASTConditionalExpression) {
            ((ASTConditionalExpression) node).getTypeMirror(); // force resolution
            return !((ASTConditionalExpression) node).isStandalone();
        }
        return node instanceof ASTSwitchExpression && child.getIndexInParent() != 0 // not the condition
            || node instanceof ASTSwitchArrowBranch
            || node instanceof ASTConditionalExpression && child.getIndexInParent() != 0 // not the condition
            // lambdas "forward the context" when you have nested lambdas, eg: `x -> y -> f(x, y)`
            || node instanceof ASTLambdaExpression && child.getIndexInParent() == 1; // the body expression
    }


    // test only
    static JTypeMirror computeStandaloneConditionalType(TypeSystem ts, JTypeMirror t2, JTypeMirror t3) {
        return computeStandaloneConditionalType(ts, asList(t2, t3));
    }

    /**
     * Compute the type of a conditional or switch expression. This is
     * how Javac does it for now, and it's exactly an extension of the
     * rules for ternary operators to an arbitrary number of branches.
     *
     * todo can we merge this into the logic of the BranchingMirror implementations?
     */
    private static JTypeMirror computeStandaloneConditionalType(TypeSystem ts, List<JTypeMirror> branchTypes) {
        // There is a corner case with constant values & ternaries, which we don't handle.

        if (branchTypes.isEmpty()) {
            return ts.OBJECT;
        }

        JTypeMirror head = branchTypes.get(0);
        List<JTypeMirror> tail = branchTypes.subList(1, branchTypes.size());

        if (all(tail, head::equals)) {
            return head;
        }


        List<JTypeMirror> unboxed = map(branchTypes, JTypeMirror::unbox);
        if (all(unboxed, JTypeMirror::isPrimitive)) {
            for (JPrimitiveType a : ts.allPrimitives) {
                if (all(unboxed, it -> it.isConvertibleTo(a).bySubtyping())) {
                    // then all types are convertible to a
                    return a;
                }
            }
        }

        List<JTypeMirror> boxed = map(branchTypes, JTypeMirror::box);
        for (JTypeMirror a : boxed) {
            if (all(unboxed, it -> isConvertibleUsingBoxing(it, a))) {
                // then all types are convertible to a through boxing
                return a;
            }
        }

        // at worse returns Object
        return ts.lub(branchTypes);
    }

}
