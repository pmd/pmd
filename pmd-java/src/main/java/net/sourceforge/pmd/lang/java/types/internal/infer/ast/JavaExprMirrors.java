/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

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
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.FunctionalExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.Infer;
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.CtorInvocMirror.EnumCtorInvocMirror;

/** Façade that creates {@link ExprMirror} instances. */
public final class JavaExprMirrors {

    final Infer infer;
    final TypeSystem ts;

    public JavaExprMirrors(Infer infer) {
        this.infer = infer;
        this.ts = infer.getTypeSystem();
    }

    public ExprMirror getMirror(ASTExpression e) {
        if (e instanceof InvocationNode) {
            return getInvocationMirror((InvocationNode) e);
        } else if (e instanceof ASTLambdaExpression || e instanceof ASTMethodReference) {
            return getFunctionalMirror(e);
        } else if (e instanceof ASTConditionalExpression) {
            return new ConditionalMirrorImpl(this, (ASTConditionalExpression) e);
        } else if (e instanceof ASTSwitchExpression) {
            return new SwitchMirror(this, (ASTSwitchExpression) e);
        } else {
            // Standalone
            return new StandaloneExprMirror(this, e);
        }
    }

    public InvocationMirror getInvocationMirror(InvocationNode e) {
        if (e instanceof ASTMethodCall) {
            return new MethodInvocMirror(this, (ASTMethodCall) e);
        } else if (e instanceof ASTConstructorCall) {
            return new CtorInvocMirror(this, (ASTConstructorCall) e);
        } else if (e instanceof ASTExplicitConstructorInvocation) {
            return new CtorInvocMirror.ExplicitCtorInvocMirror(this, (ASTExplicitConstructorInvocation) e);
        } else if (e instanceof ASTEnumConstant) {
            return new EnumCtorInvocMirror(this, (ASTEnumConstant) e);
        }
        throw new IllegalStateException("" + e);
    }

    public FunctionalExprMirror getFunctionalMirror(ASTExpression e) {
        if (e instanceof ASTLambdaExpression) {
            return new LambdaMirrorImpl(this, (ASTLambdaExpression) e);
        } else if (e instanceof ASTMethodReference) {
            return new MethodRefMirrorImpl(this, (ASTMethodReference) e);
        }
        throw new IllegalArgumentException("" + e);
    }


}
