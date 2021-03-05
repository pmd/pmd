/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

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

    ConditionalMirrorImpl(JavaExprMirrors mirrors, ASTConditionalExpression expr) {
        super(mirrors, expr);
        thenBranch = mirrors.getMirror(myNode.getThenBranch());
        elseBranch = mirrors.getMirror(myNode.getElseBranch());
    }


    @Override
    public boolean branchesMatch(Predicate<? super ExprMirror> condition) {
        return condition.test(thenBranch) && condition.test(elseBranch);
    }

    @Override
    public @Nullable JTypeMirror getStandaloneType() {
        // may have been set by an earlier call
        JTypeMirror current = InternalApiBridge.getTypeMirrorInternal(myNode);
        if (current != null && current.unbox().isPrimitive()) {
            // standalone
            return current;
        }
        JTypeMirror condType = getConditionalStandaloneType(this, myNode);
        if (condType != null) {
            InternalApiBridge.setTypeMirrorInternal(myNode, condType);
        }
        return condType;
    }


    /**
     * Conditional expressions are standalone iff both their branches
     * are of a primitive type (or a primitive wrapper type). This may
     * involve inferring the compile-time declaration of a method call.
     *
     * https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.25
     */
    private JTypeMirror getConditionalStandaloneType(ConditionalMirrorImpl mirror, ASTConditionalExpression cond) {
        JTypeMirror thenType = standaloneExprTypeInConditional(mirror.thenBranch, cond.getThenBranch());
        if (thenType == null || !thenType.unbox().isPrimitive()) {
            return null;
        }

        JTypeMirror elseType = standaloneExprTypeInConditional(mirror.elseBranch, cond.getElseBranch());

        if (elseType == null || !elseType.unbox().isPrimitive()) {
            return null;
        }

        // both are primitive or primitive wrappers

        if (elseType.unbox().equals(thenType.unbox())) {
            // eg (Integer, Integer) -> Integer but (Integer, int) -> int
            return thenType.equals(elseType) ? thenType : thenType.unbox();
        }

        if (thenType.isNumeric() == elseType.isNumeric()) {
            return TypeConversion.binaryNumericPromotion(thenType.unbox(), elseType.unbox());
        }
        return null;
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
