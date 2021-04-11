/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArguments;
import net.sourceforge.pmd.lang.java.ast.ExprContext;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.CtorInvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror.MethodCtDecl;
import net.sourceforge.pmd.lang.java.types.internal.infer.Infer;
import net.sourceforge.pmd.lang.java.types.internal.infer.MethodCallSite;
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.JavaExprMirrors;


/**
 *
 */
public class UnnecessaryTypeArgumentRule extends AbstractJavaRulechainRule {

    public UnnecessaryTypeArgumentRule() {
        super(ASTTypeArguments.class);
    }


    @Override
    public Object visit(ASTTypeArguments node, Object data) {
        JavaNode parent = node.getParent();
        if (parent instanceof ASTMethodCall) {
            checkUnnecessary((ASTMethodCall) parent, node, (RuleContext) data);
        } else if (parent instanceof ASTConstructorCall) {
            checkUnnecessary((ASTConstructorCall) parent, node, (RuleContext) data);
        }
        return null;
    }

    private <T extends InvocationNode & ASTExpression> void checkUnnecessary(T call, ASTTypeArguments targs, RuleContext data) {
        // does not check for diamond

        JExecutableSymbol currentSymbol = call.getMethodType().getSymbol();

        MethodCtDecl result = doOverloadResolutionWithoutTypeArgs(call);

        if (result.isFailed() || !result.getMethodType().getSymbol().equals(currentSymbol)) {
            return;
        }

        addViolation(data, targs);
    }

    private <T extends InvocationNode & ASTExpression> MethodCtDecl doOverloadResolutionWithoutTypeArgs(T call) {
        Infer infer = InternalApiBridge.getInferenceEntryPoint(call);
        // this may not mutate the AST
        InvocationMirror baseMirror = JavaExprMirrors.forObservation(infer).getInvocationMirror(call);
        SpyInvocMirror spyMirror = installSpy(baseMirror);
        ExprContext contextType = call.getConversionContextType();
        JTypeMirror expectedType = contextType == null ? null : contextType.getTargetType();
        MethodCallSite fakeCallSite = infer.newCallSite(spyMirror, expectedType);
        infer.inferInvocationRecursively(fakeCallSite);
        return spyMirror.result;
    }

    private boolean isSimpleEnough(InvocationNode call) {
        return true;
    }

    private SpyInvocMirror installSpy(InvocationMirror base) {
        if (base instanceof CtorInvocationMirror) {
            throw new NotImplementedException("todo");
        }
        return new SpyInvocMirror(base);
    }

    /** Proxy that pretends it has no explicit type arguments. */
    private static final class SpyInvocMirror implements ExprMirror.InvocationMirror {

        private final InvocationMirror base;
        private MethodCtDecl result;

        SpyInvocMirror(InvocationMirror base) {
            this.base = base;
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
