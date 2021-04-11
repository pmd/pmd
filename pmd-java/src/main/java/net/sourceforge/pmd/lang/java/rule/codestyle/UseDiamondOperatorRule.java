/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArguments;
import net.sourceforge.pmd.lang.java.ast.ExprContext;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.CtorInvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror.MethodCtDecl;
import net.sourceforge.pmd.lang.java.types.internal.infer.Infer;
import net.sourceforge.pmd.lang.java.types.internal.infer.MethodCallSite;
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.JavaExprMirrors;

public class UseDiamondOperatorRule extends AbstractJavaRulechainRule {

    public UseDiamondOperatorRule() {
        super(ASTConstructorCall.class);
    }

    public Object visit(ASTConstructorCall ctorCall, Object data) {
        ASTClassOrInterfaceType newTypeNode = ctorCall.getTypeNode();
        JTypeMirror newType = newTypeNode.getTypeMirror();

        ASTTypeArguments targs = newTypeNode.getTypeArguments();
        if (targs != null && targs.isDiamond()
            // if unresolved we can't know whether the class is generic or not
            || TypeOps.isUnresolved(newType)) {
            return null;
        }

        // targs may be null, in which case this would be a raw type
        if (!newType.isGeneric()) {
            return null;
        }

        ExprContext exprCtx = ctorCall.getConversionContextType();
        if (exprCtx == null || exprCtx.getTargetType() == null) {
            return null; // cannot be converted
        }

        if (ctorCall.isAnonymousClass() && !supportsDiamondOnAnonymousClass(ctorCall)) {
            return null;
        }

        JavaNode reportNode = targs == null ? newTypeNode : targs;
        checkUnnecessary(ctorCall, reportNode, (RuleContext) data);
        return null;
    }

    private boolean supportsDiamondOnAnonymousClass(ASTConstructorCall ctorCall) {
        return ctorCall.getAstInfo().getLanguageVersion().compareToVersion("9") >= 0;
    }


    private void checkUnnecessary(ASTConstructorCall call, JavaNode reportNode, RuleContext data) {

        ExprContext contextType = call.getConversionContextType();
        if (contextType == null || contextType.getTargetType() == null) {
            return;
        }
        JTypeMirror expectedType = contextType.getTargetType();
        MethodCtDecl result = doOverloadResolutionWithoutTypeArgs(call, expectedType);

        if (result.isFailed()
            || !result.getMethodType().getReturnType().isSubtypeOf(expectedType)) {
            return;
        }

        addViolation(data, reportNode);
    }

    private MethodCtDecl doOverloadResolutionWithoutTypeArgs(ASTConstructorCall call, JTypeMirror expectedType) {
        Infer infer = InternalApiBridge.getInferenceEntryPoint(call);
        SpyInvocMirror spyMirror = makeSpy(infer, call);
        MethodCallSite fakeCallSite = infer.newCallSite(spyMirror, expectedType);
        infer.inferInvocationRecursively(fakeCallSite);
        return spyMirror.result;
    }

    private SpyInvocMirror makeSpy(Infer infer, ASTConstructorCall ctorCall) {
        // this may not mutate the AST
        JavaExprMirrors factory = JavaExprMirrors.forObservation(infer);
        CtorInvocationMirror baseMirror = (CtorInvocationMirror) factory.getInvocationMirror(ctorCall);
        return new SpyInvocMirror(baseMirror, (JClassType) ctorCall.getTypeMirror());
    }

    /** Proxy that pretends it has diamond type args. */
    private static final class SpyInvocMirror implements CtorInvocationMirror {

        private final CtorInvocationMirror base;
        private final JClassType modifiedNewType;
        private MethodCtDecl result;

        SpyInvocMirror(CtorInvocationMirror base, JClassType baseNewType) {
            this.base = base;
            // see doc of CtorInvocationMirror#getNewType
            this.modifiedNewType = baseNewType.getGenericTypeDeclaration();
        }

        @Override
        public JavaNode getLocation() {
            return base.getLocation();
        }

        @Override
        public void setInferredType(JTypeMirror mirror) {
            // do nothing, we shouldn't affect the AST from here
        }

        @Override
        public @NonNull JClassType getEnclosingType() {
            return base.getEnclosingType();
        }

        @Override
        public @NonNull JTypeMirror getNewType() {
            return modifiedNewType;
        }

        @Override
        public boolean isDiamond() {
            return true; // preted it is
        }

        @Override
        public boolean isAnonymous() {
            return base.isAnonymous();
        }

        @Override
        public Iterable<JMethodSig> getAccessibleCandidates() {
            return base.getAccessibleCandidates();
        }

        @Override
        public @Nullable JTypeMirror getReceiverType() {
            return base.getReceiverType();
        }

        @Override
        public List<JTypeMirror> getExplicitTypeArguments() {
            return Collections.emptyList(); // pretend they're not there
        }

        @Override
        public JavaNode getExplicitTargLoc(int i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public String getName() {
            return base.getName();
        }

        @Override
        public List<ExprMirror> getArgumentExpressions() {
            return base.getArgumentExpressions();
        }

        @Override
        public int getArgumentCount() {
            return base.getArgumentCount();
        }

        @Override
        public void setMethodType(MethodCtDecl methodType) {
            result = methodType;
        }

        @Override
        public @Nullable MethodCtDecl getMethodType() {
            return result;
        }
    }

}
