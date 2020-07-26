/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.ast;


import static java.util.Arrays.asList;
import static net.sourceforge.pmd.lang.java.types.TypeConversion.isConvertible;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTYieldStatement;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
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

            if (ctx != e && ctx instanceof InvocationNode) {
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
            } else if (ctx == e) {
                // no surrounding context
                if (ctx instanceof InvocationNode) {
                    JTypeMirror targetType = null;
                    if (ctx instanceof ASTExplicitConstructorInvocation) {
                        // target type is superclass type
                        targetType = ctx.getEnclosingType().getTypeMirror().getSuperClass();
                    }
                    // Invocation context: infer the called method, which will cascade down this node.
                    // This branch is also taken, if e is an invocation and has no surrounding context (ctx == e).
                    return inferInvocation((InvocationNode) ctx, e, targetType);
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
                    return computeStandaloneConditionalType((ASTConditionalExpression) e);
                } else if (e instanceof ASTSwitchExpression) {
                    List<JTypeMirror> branches = ((ASTSwitchExpression) e).getYieldExpressions().toList(TypeNode::getTypeMirror);
                    return computeStandaloneConditionalType(ts, branches);
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
                    // those take directly the target type
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
    private JTypeMirror inferInvocation(InvocationNode ctxNode, TypeNode enclosed, @Nullable JTypeMirror targetType) {
        InvocationMirror mirror = exprMirrors.getInvocationMirror(ctxNode);
        MethodCallSite site = infer.newCallSite(mirror, targetType);
        @NonNull MethodCtDecl ctDecl = infer.determineInvocationType(site);
        // TODO errors are on the call site if any

        // this isn't done automatically by Infer because
        // this procedure may be internally called during
        // type inference, returning partially inferred types
        mirror.setMethodType(ctDecl);
        mirror.setInferredType(ctDecl.getMethodType().getReturnType());

        // adapt constructor call
        if (ctxNode instanceof ASTConstructorCall) {
            ASTConstructorCall ctor = (ASTConstructorCall) ctxNode;
            if (ctor.isAnonymousClass()) {
                mirror.setInferredType(ctor.getAnonymousClassDeclaration().getTypeMirror());
            }
        }

        return fetchCascaded(enclosed);
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
            // this is set if the conditional is standalone
            JTypeMirror type = InternalApiBridge.getTypeMirrorInternal(e);
            if (type != null) {
                return type;
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
                // target type
                if (parentInvoc.isVarargsCall()) {
                    JTypeMirror lastFormal = mt.getFormalParameters().get(mt.getArity() - 1);
                    return ((JArrayType) lastFormal).getComponentType();
                } else {
                    return mt.getFormalParameters().get(e.getIndexInParent());
                }
            }
        } else if (e.getParent() instanceof ASTConditionalExpression) {
            return fetchCascaded((TypeNode) e.getParent());
        }

        throw new IllegalStateException("Unknown poly? " + e);
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
    @NonNull
    private JTypeMirror getTargetType(JavaNode context) {

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
     * <p>If there is no context node, returns the parameter.
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
     * 1 + (a ? foo() : 2) //  contextOf(methodCall) = itself
     *                     //  foo() here has no target type, because the enclosing conditional has none
     *
     *
     * </pre>
     */
    private static JavaNode contextOf(JavaNode node, boolean onlyInvoc) {
        JavaNode papa = node.getParent();
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
            JavaNode papi = papa.getParent();

            if (papi instanceof ASTExplicitConstructorInvocation || papi instanceof ASTEnumConstant) {
                return papi;
            } else {
                // constructor or method call, maybe there's another context around
                JavaNode ctx = contextOf(papi, true);
                // if the cascade expr has no context itself, then it's not a context and we return the node
                return doesCascadesContext(ctx) ? node : ctx;
            }
        } else if (doesCascadesContext(papa)) {
            // switch/conditional

            JavaNode ctx = contextOf(papa, onlyInvoc);
            // if the cascade expr has no context itself, then it's not a context and we return the node
            return doesCascadesContext(ctx) ? node : ctx;
        }

        if (onlyInvoc) {
            return node;
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
            JavaNode ctx = contextOf(owner, false);
            return doesCascadesContext(ctx) ? node : owner;
        } else {
            // stop recursion
            return node;
        }
    }


    /**
     * Neither of those are contexts, rather, they pass context through their branches.
     * If their parent has no context, then they don't either.
     */
    private static boolean doesCascadesContext(JavaNode node) {
        return node instanceof ASTSwitchExpression
            || node instanceof ASTSwitchArrowBranch
            || node instanceof ASTConditionalExpression;
    }


    private JTypeMirror computeStandaloneConditionalType(ASTConditionalExpression node) {
        return computeStandaloneConditionalType(
            ts,
            node.getThenBranch().getTypeMirror(),
            node.getElseBranch().getTypeMirror()
        );
    }

    // test only
    static JTypeMirror computeStandaloneConditionalType(TypeSystem ts, JTypeMirror t2, JTypeMirror t3) {
        return computeStandaloneConditionalType(ts, asList(t2, t3));
    }

    /**
     * Compute the type of a conditional or switch expression. This is
     * how Javac does it for now, and it's exactly an extension of the
     * rules for ternary operators to an arbitrary number of branches.
     */
    private static JTypeMirror computeStandaloneConditionalType(TypeSystem ts, List<JTypeMirror> branchTypes) {
        // There is a corner case with constant values

        if (branchTypes.isEmpty()) {
            return ts.OBJECT;
        }

        JTypeMirror first = branchTypes.get(0);

        if (branchTypes.stream().skip(1).allMatch(first::equals)) {
            return first;
        }


        List<JTypeMirror> unboxed = branchTypes.stream().map(JTypeMirror::unbox).collect(Collectors.toList());
        if (unboxed.stream().allMatch(JTypeMirror::isPrimitive)) {
            for (JPrimitiveType a : ts.allPrimitives) {
                if (unboxed.stream().allMatch(it -> it.isSubtypeOf(a))) {
                    // then all types are convertible to a
                    return a;
                }
            }
        }

        List<JTypeMirror> boxed = branchTypes.stream().map(JTypeMirror::box).collect(Collectors.toList());
        for (JTypeMirror a : boxed) {
            if (unboxed.stream().allMatch(it -> isConvertible(it, a))) {
                // then all types are convertible to a
                return a;
            }
        }

        // at worse returns Object
        return ts.lub(branchTypes);
    }


}
