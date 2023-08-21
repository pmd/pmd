/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.ast;


import static java.util.Arrays.asList;
import static net.sourceforge.pmd.lang.java.types.TypeConversion.isConvertibleUsingBoxing;
import static net.sourceforge.pmd.util.AssertionUtil.shouldNotReachHere;
import static net.sourceforge.pmd.util.CollectionUtil.all;
import static net.sourceforge.pmd.util.CollectionUtil.map;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAccess;
import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTAssertStatement;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVoidType;
import net.sourceforge.pmd.lang.java.ast.ASTYieldStatement;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypeConversion;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.java.types.TypesFromReflection;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext.ExprContextKind;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext.RegularCtx;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.BranchingMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.FunctionalExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.Infer;
import net.sourceforge.pmd.lang.java.types.internal.infer.MethodCallSite;
import net.sourceforge.pmd.lang.java.types.internal.infer.PolySite;
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.JavaExprMirrors;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * Routines to handle context around poly expressions.
 */
final class PolyResolution {

    private final Infer infer;
    private final TypeSystem ts;
    private final JavaExprMirrors exprMirrors;
    private final ExprContext booleanCtx;
    private final ExprContext stringCtx;
    private final ExprContext intCtx;

    PolyResolution(Infer infer) {
        this.infer = infer;
        this.ts = infer.getTypeSystem();
        this.exprMirrors = JavaExprMirrors.forTypeResolution(infer);

        this.stringCtx = newStringCtx(ts);
        this.booleanCtx = newNonPolyContext(ts.BOOLEAN);
        this.intCtx = newNumericContext(ts.INT);
    }

    private boolean isPreJava8() {
        return infer.isPreJava8();
    }

    JTypeMirror computePolyType(final TypeNode e) {
        if (!canBePoly(e)) {
            throw shouldNotReachHere("Unknown poly " + e);
        }

        ExprContext ctx = getTopLevelConversionContext(e);

        InvocationNode outerInvocNode = ctx.getInvocNodeIfInvocContext();
        if (outerInvocNode != null) {
            return polyTypeInvocationCtx(e, outerInvocNode);
        }

        return polyTypeOtherCtx(e, ctx);
    }

    private JTypeMirror polyTypeOtherCtx(TypeNode e, ExprContext ctx) {
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
                return computeStandaloneConditionalType(
                    this.ts,
                    conditional.getThenBranch().getTypeMirror(),
                    conditional.getElseBranch().getTypeMirror()
                );
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
                BranchingMirror polyMirror = exprMirrors.getPolyBranchingMirror((ASTExpression) e);
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

    // only outside of invocation context
    private JTypeMirror inferLambdaOrMref(ASTExpression e, @Nullable JTypeMirror targetType) {
        FunctionalExprMirror mirror = exprMirrors.getTopLevelFunctionalMirror(e);
        PolySite<FunctionalExprMirror> site = infer.newFunctionalSite(mirror, targetType);
        infer.inferFunctionalExprInUnambiguousContext(site);
        JTypeMirror result = InternalApiBridge.getTypeMirrorInternal(e);
        assert result != null : "Should be unknown";
        return result;
    }

    private @NonNull JTypeMirror polyTypeInvocationCtx(TypeNode e, InvocationNode ctxInvoc) {
        // an outer invocation ctx
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
        InvocationMirror mirror = exprMirrors.getTopLevelInvocationMirror(ctxNode);
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
        return polyTypeOtherCtx(e, ExprContext.getMissingInstance());
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

    /**
     * Fallback for some standalone expressions, that may use some context
     * to set their type. This must not trigger any type inference process
     * that may need this expression. So if this expression is in an invocation
     * context, that context must not be called.
     */
    JTypeMirror getContextTypeForStandaloneFallback(ASTExpression e) {
        // Some symbol is not resolved
        // go backwards from the context to get it.

        // The case mentioned by the doc is removed. We could be smarter
        // with how we retry failed invocation resolution, see history
        // of this comment

        @NonNull ExprContext ctx = getTopLevelConversionContext(e);

        if (e.getParent() instanceof ASTSwitchLabel) {
            ASTSwitchLike switchLike = e.ancestors(ASTSwitchLike.class).firstOrThrow();
            // this may trigger some inference, which doesn't matter
            // as it is out of context
            return switchLike.getTestedExpression().getTypeMirror();
        }

        if (ctx instanceof RegularCtx) {
            JTypeMirror targetType = ctx.getPolyTargetType(false);
            if (targetType != null) {
                return targetType;
            }
        }

        return ts.UNKNOWN;
    }

    /**
     * Not meant to be used by the main typeres paths, only for rules.
     */
    ExprContext getConversionContextForExternalUse(ASTExpression e) {
        return contextOf(e, false, false);
    }

    ExprContext getTopLevelConversionContext(TypeNode e) {
        return contextOf(e, false, true);
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
                return ExprContext.newInvocContext(papi, node.getIndexInParent());
            } else {
                if (isPreJava8()) {
                    // in java < 8 invocation contexts don't provide a target type
                    return ExprContext.getMissingInstance();
                }

                // Constructor or method call, maybe there's another context around
                // We want to fetch the outermost invocation node, but not further
                ExprContext outerCtx = contextOf(papi, /*onlyInvoc:*/true, internalUse);
                return outerCtx.canGiveContextToPoly(false)
                       ? outerCtx
                       // otherwise we're done, this is the outermost context
                       : ExprContext.newInvocContext(papi, node.getIndexInParent());
            }
        } else if (doesCascadesContext(papa, node, internalUse)) {
            // switch/conditional
            return contextOf(papa, onlyInvoc, internalUse);
        }

        if (onlyInvoc) {
            return ExprContext.getMissingInstance();
        }

        if (papa instanceof ASTArrayInitializer) {

            JTypeMirror target = TypeOps.getArrayComponent(((ASTArrayInitializer) papa).getTypeMirror());
            return newAssignmentCtx(target);

        } else if (papa instanceof ASTCastExpression) {

            JTypeMirror target = ((ASTCastExpression) papa).getCastType().getTypeMirror();
            return newCastCtx(target);

        } else if (papa instanceof ASTAssignmentExpression && node.getIndexInParent() == 1) { // second operand

            JTypeMirror target = ((ASTAssignmentExpression) papa).getLeftOperand().getTypeMirror();
            return newAssignmentCtx(target);

        } else if (papa instanceof ASTReturnStatement) {

            return newAssignmentCtx(returnTargetType((ASTReturnStatement) papa));

        } else if (papa instanceof ASTVariableDeclarator
            && !((ASTVariableDeclarator) papa).getVarId().isTypeInferred()) {

            return newAssignmentCtx(((ASTVariableDeclarator) papa).getVarId().getTypeMirror());

        } else if (papa instanceof ASTYieldStatement) {

            // break with value (switch expr)
            ASTSwitchExpression owner = ((ASTYieldStatement) papa).getYieldTarget();
            return contextOf(owner, false, internalUse);

        } else if (node instanceof ASTExplicitConstructorInvocation
            && ((ASTExplicitConstructorInvocation) node).isSuper()) {

            // the superclass type is taken as a target type for inference,
            // when the super ctor is generic/ the superclass is generic
            return newSuperCtorCtx(node.getEnclosingType().getTypeMirror().getSuperClass());

        }

        if (!internalUse) {
            // Only ASTExpression#getConversionContext needs this level of detail
            // These anyway do not give a context to poly expression so can be ignored
            // for poly resolution.
            return conversionContextOf(node, papa);
        }

        // stop recursion
        return ExprContext.getMissingInstance();
    }

    // more detailed
    private ExprContext conversionContextOf(JavaNode node, JavaNode papa) {
        if (papa instanceof ASTArrayAccess && node.getIndexInParent() == 1) {

            // array index
            return intCtx;

        } else if (papa instanceof ASTAssertStatement) {

            return node.getIndexInParent() == 0 ? booleanCtx // condition
                                                : stringCtx; // message

        } else if (papa instanceof ASTIfStatement
            || papa instanceof ASTLoopStatement && !(papa instanceof ASTForeachStatement)) {

            return booleanCtx; // condition

        } else if (papa instanceof ASTConditionalExpression) {

            if (node.getIndexInParent() == 0) {
                return booleanCtx; // the condition
            } else {
                // a branch
                if (isPreJava8()) {
                    return ExprContext.getMissingInstance();
                }
                assert InternalApiBridge.isStandaloneInternal((ASTConditionalExpression) papa)
                    : "Expected standalone ternary, otherwise doesCascadeContext(..) would have returned true";

                return newStandaloneTernaryCtx(((ASTConditionalExpression) papa).getTypeMirror());
            }

        } else if (papa instanceof ASTInfixExpression) {
            // numeric contexts, maybe
            BinaryOp op = ((ASTInfixExpression) papa).getOperator();
            JTypeMirror nodeType = ((ASTExpression) node).getTypeMirror();
            JTypeMirror otherType = JavaAstUtils.getOtherOperandIfInInfixExpr(node).getTypeMirror();
            JTypeMirror ctxType = ((ASTInfixExpression) papa).getTypeMirror();
            switch (op) {
            case CONDITIONAL_OR:
            case CONDITIONAL_AND:
                return booleanCtx;
            case OR:
            case XOR:
            case AND:
                return ctxType == ts.BOOLEAN ? booleanCtx : newNumericContext(ctxType); // NOPMD CompareObjectsWithEquals
            case LEFT_SHIFT:
            case RIGHT_SHIFT:
            case UNSIGNED_RIGHT_SHIFT:
                return node.getIndexInParent() == 1 ? intCtx
                                                    : newNumericContext(nodeType.unbox());
            case EQ:
            case NE:
                if (otherType.isPrimitive() != nodeType.isPrimitive()) {
                    return newNonPolyContext(otherType.unbox());
                }
                return ExprContext.getMissingInstance();
            case ADD:
                if (TypeTestUtil.isA(String.class, ctxType)) {
                    // string concat expr
                    return stringCtx;
                }
                // fallthrough
            case SUB:
            case MUL:
            case DIV:
            case MOD:
                return newNumericContext(ctxType); // binary promoted by LazyTypeResolver
            case LE:
            case GE:
            case GT:
            case LT:
                return newNumericContext(TypeConversion.binaryNumericPromotion(nodeType, otherType));
            default:
                return ExprContext.getMissingInstance();
            }
        } else {
            return ExprContext.getMissingInstance();
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
            // in java < 8, context doesn't flow through ternaries
            return false;
        } else if (!internalUse
            && node instanceof ASTConditionalExpression
            && child.getIndexInParent() != 0) {
            // conditional branch
            ((ASTConditionalExpression) node).getTypeMirror(); // force resolution
            return !InternalApiBridge.isStandaloneInternal((ASTConditionalExpression) node);
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

    static ExprContext newAssignmentCtx(JTypeMirror targetType) {
        if (targetType == null) {
            // invalid syntax
            return ExprContext.getMissingInstance();
        }
        return ExprContext.newOtherContext(targetType, ExprContextKind.ASSIGNMENT);
    }

    static ExprContext newNonPolyContext(JTypeMirror targetType) {
        return ExprContext.newOtherContext(targetType, ExprContextKind.BOOLEAN);
    }

    static ExprContext newStringCtx(TypeSystem ts) {
        JClassType stringType = (JClassType) TypesFromReflection.fromReflect(String.class, ts);
        return ExprContext.newOtherContext(stringType, ExprContextKind.STRING);
    }

    static ExprContext newNumericContext(JTypeMirror targetType) {
        if (targetType.isPrimitive()) {
            assert targetType.isNumeric() : "Not a numeric type - " + targetType;
            return ExprContext.newOtherContext(targetType, ExprContextKind.NUMERIC);
        }
        return ExprContext.getMissingInstance(); // error
    }

    static ExprContext newCastCtx(JTypeMirror targetType) {
        return ExprContext.newOtherContext(targetType, ExprContextKind.CAST);
    }

    static ExprContext newSuperCtorCtx(JTypeMirror superclassType) {
        return ExprContext.newOtherContext(superclassType, ExprContextKind.ASSIGNMENT);
    }

    static ExprContext newStandaloneTernaryCtx(JTypeMirror ternaryType) {
        return ExprContext.newOtherContext(ternaryType, ExprContextKind.TERNARY);
    }
}
