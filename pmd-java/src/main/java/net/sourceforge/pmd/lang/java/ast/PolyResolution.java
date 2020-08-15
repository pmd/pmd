/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;


import static java.util.Arrays.asList;
import static net.sourceforge.pmd.lang.java.types.TypeConversion.isConvertible;
import static net.sourceforge.pmd.util.CollectionUtil.all;
import static net.sourceforge.pmd.util.CollectionUtil.map;

import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror.MethodCtDecl;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.PolyExprMirror;
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
        this.exprMirrors = new JavaExprMirrors(infer);
    }

    /*
        TODO array initializers count as assignment context:
         Runnable[] r = { () -> {} };
     */

    JTypeMirror computePolyType(TypeNode e) {
        if (canBePoly(e)) {
            // here ctx can be
            // - e (no context)
            // - an InvocationNode -> invocation context
            // - a ReturnStatement, AssignmentExpression, VariableDeclarator -> assignment context
            // - a CastExpression -> cast context
            JavaNode ctx = contextOf(e, false);

            if (ctx instanceof InvocationNode) {
                // an outer invocation ctx
                if (ctx instanceof ASTExpression) {
                    // method call or regular constructor call
                    // recurse, that will fetch the outer context
                    // FIXME this is not true if resolution of the context failed
                    ((ASTExpression) ctx).getTypeMirror();
                    return fetchCascaded(e);
                } else {
                    return inferInvocation((InvocationNode) ctx, e, null);
                }
            } else if (ctx == null) { // NOPMD CompareObjectsWithEquals
                // no surrounding context
                if (e instanceof InvocationNode) {
                    JTypeMirror targetType = null;
                    if (e instanceof ASTExplicitConstructorInvocation) {
                        // target type is superclass type
                        targetType = e.getEnclosingType().getTypeMirror().getSuperClass();
                    }
                    // Invocation context: infer the called method, which will cascade down this node.
                    // This branch is also taken, if e is an invocation and has no surrounding context (ctx == e).
                    return inferInvocation((InvocationNode) e, e, targetType);
                }

                // no context for a poly that's not an invocation? This could either mean
                // - a lambda or mref that occurs outside of a cast, invoc, or assignment context
                //   -> semantic error
                // - a conditional or switch that is outside an assignment or invocation context,
                // in which case either
                //   - it occurs outside of the contexts where it is allowed, eg a conditional as
                //   an expression statement (for switch it's ok, simply a SwitchStatement in this case)
                //      -> parsing error, cannot occur at this stage
                //   - it is not a poly, which is ok (eg 1 + (a ? 2 : 3) )
                // - a poly in a var assignment context: var f = a ? b : c;
                if (e instanceof ASTConditionalExpression) {
                    return computeStandaloneConditionalType((ASTConditionalExpression) e, false);
                } else if (e instanceof ASTSwitchExpression) {
                    List<JTypeMirror> branches = ((ASTSwitchExpression) e).getYieldExpressions().toList(TypeNode::getTypeMirror);
                    return computeConditionalType(ts, branches, false);
                } else {
                    return ts.ERROR_TYPE;
                }
            } else {
                // we have a context, that is not an invocation
                JTypeMirror targetType = getTargetType(ctx);
                if (e instanceof InvocationNode) {
                    // The original expr was an invocation, but we have
                    // a context type (eg assignment context)
                    return inferInvocation((InvocationNode) e, e, targetType);
                } else if (e instanceof ASTMethodReference || e instanceof ASTLambdaExpression) {
                    PolyExprMirror mirror = (PolyExprMirror) exprMirrors.getMirror((ASTExpression) e);
                    PolySite site = infer.newPolySite(mirror, targetType);
                    infer.inferLambdaOrMrefInUnambiguousContext(site);
                    return fetchCascaded(e);
                } else if (e instanceof ASTSwitchExpression || e instanceof ASTConditionalExpression) {
                    // Those take directly the target type, if there is one
                    // otherwise they're standalone expressions
                    return targetType;
                } else {
                    throw new IllegalStateException("Unknown poly?");
                }
            }
        } else {
            throw new IllegalStateException("Unknown poly?");
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
        @NonNull MethodCtDecl ctDecl = infer.determineInvocationType(site);
        // TODO errors are on the call site if any

        // this isn't done automatically by Infer because
        // this procedure may be internally called during
        // type inference, returning partially inferred types
        mirror.setMethodType(ctDecl);
        mirror.setInferredType(ctDecl.getMethodType().getReturnType());

        return fetchCascaded(actualResultTarget);
    }

    // TODO don't use IllegalStateException, log

    /**
     * Fetch the resolved value when it was inferred as part of overload
     * resolution of an enclosing invocation context.
     */
    private JTypeMirror fetchCascaded(TypeNode e) {
        if (e instanceof ASTLambdaExpression
            || e instanceof ASTMethodReference
            || e instanceof InvocationNode) {
            // those are set as part of overload resolution

            // we need to check the value of the field,
            // if we called getTypeMirror, and the field is null,
            // will recurse into this method (so use internal fetch method)
            JTypeMirror type = InternalApiBridge.getTypeMirrorInternal(e);
            if (type == null) {
                // The type is set if the overload resolution ended correctly
                // If not the process may have been aborted early
                // TODO might log this
                return ts.UNRESOLVED_TYPE;
            }
            return type;
        }

        if (e instanceof ASTConditionalExpression) {
            // Prefer a standalone primitive type if possible, which matches the JLS more closely
            JTypeMirror standalone = computeStandaloneConditionalType(
                (ASTConditionalExpression) e,
                /*mustBePrimitive*/true
            );
            if (standalone != null) {
                return standalone;
            }
            // otherwise fallthrough
        }

        if (e.getParent().getParent() instanceof InvocationNode) {
            InvocationNode parentInvoc = (InvocationNode) e.getParent().getParent();
            JMethodSig mt = parentInvoc.getMethodType();
            if (mt == null || mt == ts.UNRESOLVED_METHOD) {
                // TODO might log this
                return ts.UNRESOLVED_TYPE;
            } else if (e instanceof ASTConditionalExpression || e instanceof ASTSwitchExpression) {
                return nthVarargParam(parentInvoc, mt, e.getIndexInParent());
            }
        } else if (e.getParent() instanceof ASTConditionalExpression) {
            return fetchCascaded((TypeNode) e.getParent());
        }

        throw new IllegalStateException("Unknown poly? " + e);
    }

    private JTypeMirror nthVarargParam(InvocationNode parentInvoc, JMethodSig mt, int paramIdx) {
        List<JTypeMirror> formals = mt.getFormalParameters();
        if (parentInvoc.isVarargsCall() && paramIdx >= formals.size()) {
            JTypeMirror lastFormal = formals.get(mt.getArity() - 1);
            return ((JArrayType) lastFormal).getComponentType();
        } else if (paramIdx > formals.size()) {
            return ts.UNRESOLVED_TYPE;
        } else {
            return formals.get(paramIdx);
        }
    }

    /**
     * If true, the expression may depends on its target type. There may not
     * be a target type though - this is given by the {@link #contextOf(JavaNode, boolean)}.
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
     * Returns the target type bestowed by the given context.
     * If the context was an {@link InvocationNode}, it should go elsewhere.
     */
    private @NonNull JTypeMirror getTargetType(JavaNode context) {

        if (context instanceof ASTReturnStatement) {
            // assignment context

            Node methodDecl =
                context.ancestors().first(
                    it -> it instanceof ASTMethodDeclaration
                        || it instanceof ASTLambdaExpression
                        || it instanceof ASTAnyTypeDeclaration
                );

            if (methodDecl == null || methodDecl instanceof ASTAnyTypeDeclaration) {
                // in initializer, or constructor decl, return with expression is forbidden
                return ts.ERROR_TYPE;
            } else if (methodDecl instanceof ASTLambdaExpression) {
                // return within a lambda
                // "assignment context", deferred to lambda inference
                JMethodSig fun = ((ASTLambdaExpression) methodDecl).getFunctionalMethod();
                return fun == null ? ts.UNRESOLVED_TYPE : fun.getReturnType();
            } else {
                ASTResultType resultType = ((ASTMethodDeclaration) methodDecl).getResultType();
                // in void method, return with expression is forbidden
                return resultType.getTypeNode() == null ? ts.ERROR_TYPE
                                                        : resultType.getTypeNode().getTypeMirror();
            }
        } else if (context instanceof ASTAssignmentExpression) {
            // assignment context
            return ((ASTAssignmentExpression) context).getLeftOperand().getTypeMirror();
        } else if (context instanceof ASTVariableDeclarator) {
            ASTType type = ((ASTVariableDeclarator) context).getVarId().getTypeNode();
            return Objects.requireNonNull(type, "For inferred type contextOf() should not return null").getTypeMirror();
        } else if (context instanceof ASTCastExpression) {
            return ((ASTCastExpression) context).getCastType().getTypeMirror();
        } else {
            throw new IllegalStateException("No target type");
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
    private static @Nullable JavaNode contextOf(JavaNode node, boolean onlyInvoc) {
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
            final JavaNode papi = papa.getParent();

            if (papi instanceof ASTExplicitConstructorInvocation || papi instanceof ASTEnumConstant) {
                return papi;
            } else {
                // Constructor or method call, maybe there's another context around
                // We want to fetch the outermost invocation node, but not further
                JavaNode outerCtx = contextOf(papi, /*onlyInvoc:*/true);
                return outerCtx == null ? papi : outerCtx;
            }
        } else if (doesCascadesContext(papa, node)) {
            // switch/conditional
            return contextOf(papa, onlyInvoc);
        }

        if (onlyInvoc) {
            return null;
        }

        if (papa instanceof ASTCastExpression) {
            // cast context
            return papa;
        } else if (papa instanceof ASTAssignmentExpression && node.getIndexInParent() == 1) { // second operand
            return papa;
        } else if (papa instanceof ASTReturnStatement || papa instanceof ASTVariableDeclarator
            && !((ASTVariableDeclarator) papa).getVarId().isTypeInferred()) {
            // this counts as assignment context
            return papa;
        } else if (papa instanceof ASTYieldStatement) {
            // break with value (switch expr)
            ASTSwitchExpression owner = ((ASTYieldStatement) papa).getYieldTarget();
            return contextOf(owner, false);
        } else {
            // stop recursion
            return null;
        }
    }


    /**
     * Identifies a node that can forward an invocation/assignment context
     * inward. If their parent has no context, then they don't either.
     */
    private static boolean doesCascadesContext(JavaNode node, JavaNode child) {
        if (child.getParent() != node) {
            // means the "node" is a "stop recursion because no context" result in contextOf
            return false;
        }
        return node instanceof ASTSwitchExpression && child.getIndexInParent() != 0 // not the condition
            || node instanceof ASTSwitchArrowBranch
            || node instanceof ASTConditionalExpression && child.getIndexInParent() != 0; // not the condition
    }


    private JTypeMirror computeStandaloneConditionalType(ASTConditionalExpression node, boolean mustBePrimitive) {
        return computeConditionalType(
            ts,
            asList(
                node.getThenBranch().getTypeMirror(),
                node.getElseBranch().getTypeMirror()
            ),
            mustBePrimitive
        );
    }

    // test only
    static JTypeMirror computeStandaloneConditionalType(TypeSystem ts, JTypeMirror t2, JTypeMirror t3) {
        return computeConditionalType(ts, asList(t2, t3), false);
    }

    /**
     * Compute the type of a conditional or switch expression. This is
     * how Javac does it for now, and it's exactly an extension of the
     * rules for ternary operators to an arbitrary number of branches.
     */
    private static @Nullable JTypeMirror computeConditionalType(TypeSystem ts, List<JTypeMirror> branchTypes, boolean mustBePrimitive) {
        // There is a corner case with constant values

        if (branchTypes.isEmpty()) {
            return ts.OBJECT;
        }

        JTypeMirror head = branchTypes.get(0);
        List<JTypeMirror> tail = branchTypes.subList(1, branchTypes.size());

        if (all(tail, head::equals)) {
            return !mustBePrimitive || head.isPrimitive() ? head : null;
        }


        List<JTypeMirror> unboxed = map(branchTypes, JTypeMirror::unbox);
        if (all(unboxed, JTypeMirror::isPrimitive)) {
            for (JPrimitiveType a : ts.allPrimitives) {
                if (all(unboxed, it -> it.isSubtypeOf(a))) {
                    // then all types are convertible to a
                    return a;
                }
            }
        }

        if (mustBePrimitive) {
            return null;
        }

        List<JTypeMirror> boxed = map(branchTypes, JTypeMirror::box);
        for (JTypeMirror a : boxed) {
            if (all(unboxed, it -> isConvertible(it, a))) {
                // then all types are convertible to a
                return a;
            }
        }

        // at worse returns Object
        return ts.lub(branchTypes);
    }


}
