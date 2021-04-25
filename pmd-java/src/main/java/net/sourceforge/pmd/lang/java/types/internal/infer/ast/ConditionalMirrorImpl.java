/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeConversion;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.BranchingMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.MethodCallSite;

class ConditionalMirrorImpl extends BasePolyMirror<ASTConditionalExpression> implements BranchingMirror {

    ExprMirror thenBranch;
    ExprMirror elseBranch;
    private final boolean mayBePoly;

    ConditionalMirrorImpl(JavaExprMirrors mirrors, ASTConditionalExpression expr, boolean isStandalone) {
        super(mirrors, expr);
        thenBranch = mirrors.getPolyMirror(myNode.getThenBranch(), isStandalone);
        elseBranch = mirrors.getPolyMirror(myNode.getElseBranch(), isStandalone);
        this.mayBePoly = !isStandalone;
    }


    @Override
    public boolean branchesMatch(Predicate<? super ExprMirror> condition) {
        return condition.test(thenBranch) && condition.test(elseBranch);
    }

    @Override
    public void setStandalone() {
        if (factory.mayMutateAst()) {
            InternalApiBridge.setStandaloneTernary(myNode);
        }
    }

    @Override
    public @Nullable JTypeMirror getStandaloneType() {
        // may have been set by an earlier call
        JTypeMirror current = InternalApiBridge.getTypeMirrorInternal(myNode);
        if (current != null && (current.unbox().isPrimitive() || !mayBePoly)) {
            // standalone
            return current;
        }
        JTypeMirror condType = getConditionalStandaloneType(this, myNode);
        if (condType != null) {
            InternalApiBridge.setTypeMirrorInternal(myNode, condType);
        }
        assert mayBePoly || condType != null : "This conditional expression is standalone!";
        return condType;
    }


    /**
     * Conditional expressions are standalone iff both their branches
     * are of a primitive type (or a primitive wrapper type), or they
     * appear in a cast context. This may involve inferring the compile-time
     * declaration of a method call.
     *
     * https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.25
     */
    private JTypeMirror getConditionalStandaloneType(ConditionalMirrorImpl mirror, ASTConditionalExpression cond) {
        @Nullable JTypeMirror thenType = standaloneExprTypeInConditional(mirror.thenBranch, cond.getThenBranch());
        if (mayBePoly && (thenType == null || !thenType.unbox().isPrimitive())) {
            return null; // then it's a poly
        }

        @Nullable JTypeMirror elseType = standaloneExprTypeInConditional(mirror.elseBranch, cond.getElseBranch());

        if (mayBePoly && (elseType == null || !elseType.unbox().isPrimitive())) {
            return null; // then it's a poly
        }

        if (thenType == null || elseType == null) {
            // this is a standalone conditional (mayBePoly == false),
            // otherwise we would have returned null early.
            if (thenType == null ^ elseType == null) {
                return thenType == null ? elseType : thenType; // the one that is non-null
            }
            return factory.ts.NULL_TYPE;
        }
        // both are non-null
        // this is a standalone, the following returns non-null

        if (elseType.unbox().equals(thenType.unbox())) {
            // eg (Integer, Integer) -> Integer but (Integer, int) -> int
            return thenType.equals(elseType) ? thenType : thenType.unbox();
        }

        if (thenType.isNumeric() && elseType.isNumeric()) {
            return TypeConversion.binaryNumericPromotion(thenType.unbox(), elseType.unbox());
        }

        // Otherwise, the second and third operands are of types S1 and S2 respectively. Let T1
        // be the type that results from applying boxing conversion to S1, and let T2 be the type
        // that results from applying boxing conversion to S2. The type of the conditional expression
        // is the result of applying capture conversion (§5.1.10) to lub(T1, T2).
        return TypeConversion.capture(factory.ts.lub(listOf(thenType.box(), elseType.box())));
    }


    private JTypeMirror standaloneExprTypeInConditional(ExprMirror mirror, ASTExpression e) {

        if (mirror instanceof StandaloneExprMirror) {
            // An expression of a standalone form (§15.2) that has type boolean or Boolean.
            // An expression of a standalone form (§15.2) with a type that is convertible to a numeric type (§4.2, §5.1.8).

            return mirror.getStandaloneType();
        }

        if (mirror instanceof CtorInvocationMirror) {
            // A class instance creation expression (§15.9) for class Boolean.
            // A class instance creation expression (§15.9) for a class that is convertible to a numeric type.
            return ((CtorInvocationMirror) mirror).getNewType().unbox();
        }

        if (mirror instanceof BranchingMirror) {
            // A boolean conditional expression.
            // A numeric conditional expression.
            return mirror.getStandaloneType();
        }

        if (e instanceof ASTMethodCall) {
            /*
                A method invocation expression (§15.12) for which the chosen most specific method (§15.12.2.5) has return type boolean or Boolean.
                Note that, for a generic method, this is the type before instantiating the method's type arguments.

            */
            JTypeMirror current = InternalApiBridge.getTypeMirrorInternal(e);
            if (current != null) {
                // don't redo the compile-time decl resolution
                // The CTDecl is cached on the mirror, not the node
                return current;
            }

            MethodCallSite site = factory.infer.newCallSite((InvocationMirror) mirror, null);

            return factory.infer.getCompileTimeDecl(site)
                                .getMethodType()
                                .getReturnType();
        }

        return null;
    }
}
