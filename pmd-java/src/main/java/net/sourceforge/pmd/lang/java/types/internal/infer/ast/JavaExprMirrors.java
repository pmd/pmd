/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.BranchingMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.FunctionalExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.Infer;
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.CtorInvocMirror.EnumCtorInvocMirror;
import net.sourceforge.pmd.util.AssertionUtil;

/** Façade that creates {@link ExprMirror} instances. */
public final class JavaExprMirrors {

    final Infer infer;
    final TypeSystem ts;
    private final boolean mayMutateAst;
    private final MirrorMaker defaultSubexprMaker = this::makeSubexprDefault;

    private JavaExprMirrors(Infer infer, boolean mayMutateAst) {
        this.infer = infer;
        this.ts = infer.getTypeSystem();
        this.mayMutateAst = mayMutateAst;
    }


    public MirrorMaker defaultMirrorMaker() {
        return defaultSubexprMaker;
    }


    /**
     * This will mutate the AST, only one must be used per compilation unit.
     */
    public static JavaExprMirrors forTypeResolution(Infer infer) {
        return new JavaExprMirrors(infer, true);
    }

    /**
     * The mirrors produced by this factory will not be able to mutate
     * the AST. This lets the mirror be decorated to "pretend" the expression
     * is something slightly different, without corrupting the data in the AST.
     */
    public static JavaExprMirrors forObservation(Infer infer) {
        return new JavaExprMirrors(infer, false);
    }

    boolean mayMutateAst() {
        return mayMutateAst;
    }

    ExprMirror makeSubexprDefault(ASTExpression e, @Nullable ExprMirror parent, MirrorMaker subexprMaker) {
        if (e instanceof InvocationNode node) {
            return getInvocationMirror(node, parent, false, subexprMaker);
        } else if (e instanceof ASTLambdaExpression || e instanceof ASTMethodReference) {
            return getFunctionalMirror(e, parent, subexprMaker);
        } else if (e instanceof ASTConditionalExpression expression) {
            return new ConditionalMirrorImpl(this, expression, false, parent, subexprMaker);
        } else if (e instanceof ASTSwitchExpression expression) {
            return new SwitchMirror(this, expression, false, parent, subexprMaker);
        } else {
            // Standalone
            return new StandaloneExprMirror(this, e, parent);
        }
    }

    ExprMirror getBranchMirrorSubexpression(ASTExpression e, boolean mustBeStandalone, @NonNull BranchingMirror parent, MirrorMaker subexprMaker) {
        if (e instanceof ASTConditionalExpression expression) {
            return new ConditionalMirrorImpl(this, expression, mustBeStandalone, parent, subexprMaker);
        } else if (e instanceof ASTSwitchExpression expression) {
            return new SwitchMirror(this, expression, mustBeStandalone, parent, subexprMaker);
        } else if (e instanceof InvocationNode node) {
            return getInvocationMirror(node, parent, mustBeStandalone, subexprMaker);
        } else {
            return subexprMaker.createMirrorForSubexpression(e, parent, subexprMaker);
        }
    }

    public InvocationMirror getTopLevelInvocationMirror(InvocationNode e) {
        return getInvocationMirror(e, defaultMirrorMaker());
    }

    public InvocationMirror getInvocationMirror(InvocationNode e, MirrorMaker subexprMaker) {
        return getInvocationMirror(e, null, false, subexprMaker);
    }

    private InvocationMirror getInvocationMirror(InvocationNode e, @Nullable ExprMirror parent,
                                                 boolean mustBeStandalone, MirrorMaker subexprMaker) {
        if (e instanceof ASTMethodCall call) {
            return new MethodInvocMirror(this, call, mustBeStandalone, parent, subexprMaker);
        } else if (e instanceof ASTConstructorCall call) {
            return new CtorInvocMirror(this, call, mustBeStandalone, parent, subexprMaker);
        } else if (e instanceof ASTExplicitConstructorInvocation invocation) {
            return new CtorInvocMirror.ExplicitCtorInvocMirror(this, invocation, parent, subexprMaker);
        } else if (e instanceof ASTEnumConstant constant) {
            return new EnumCtorInvocMirror(this, constant, parent, subexprMaker);
        }
        throw AssertionUtil.shouldNotReachHere("Unhandled InvocationNode:" + e);
    }


    /**
     * A mirror that implements the rules for standalone conditional
     * expressions correctly. getStandaloneType will work differently
     * than the one yielded by {@link #getPolyBranchingMirror(ASTExpression)}
     */
    public BranchingMirror getStandaloneBranchingMirror(ASTExpression e) {
        if (e instanceof ASTConditionalExpression expression) {
            return new ConditionalMirrorImpl(this, expression, true, null, defaultMirrorMaker());
        } else if (e instanceof ASTSwitchExpression expression) {
            return new SwitchMirror(this, expression, true, null, defaultMirrorMaker());
        }
        throw AssertionUtil.shouldNotReachHere("Unhandled expression: " + e);
    }

    /**
     * @see #getStandaloneBranchingMirror(ASTExpression)
     */
    public BranchingMirror getPolyBranchingMirror(ASTExpression e) {
        if (e instanceof ASTConditionalExpression expression) {
            return new ConditionalMirrorImpl(this, expression, false, null, defaultMirrorMaker());
        } else if (e instanceof ASTSwitchExpression expression) {
            return new SwitchMirror(this, expression, false, null, defaultMirrorMaker());
        }
        throw AssertionUtil.shouldNotReachHere("Unhandled expression: " + e);
    }

    public FunctionalExprMirror getTopLevelFunctionalMirror(ASTExpression e) {
        return getFunctionalMirror(e, null, defaultMirrorMaker());
    }

    FunctionalExprMirror getFunctionalMirror(ASTExpression e, @Nullable ExprMirror parent, MirrorMaker subexprMaker) {
        if (e instanceof ASTLambdaExpression expression) {
            return new LambdaMirrorImpl(this, expression, parent, subexprMaker);
        } else if (e instanceof ASTMethodReference reference) {
            return new MethodRefMirrorImpl(this, reference, parent, subexprMaker);
        }
        throw AssertionUtil.shouldNotReachHere("Unhandled expression: " + e);
    }


    @FunctionalInterface
    public interface MirrorMaker {

        ExprMirror createMirrorForSubexpression(ASTExpression e, ExprMirror parent, MirrorMaker self);
    }

}
