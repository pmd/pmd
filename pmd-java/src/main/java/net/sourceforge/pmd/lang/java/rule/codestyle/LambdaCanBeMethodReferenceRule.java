/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTClassLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaParameterList;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSuperExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThisExpression;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypingContext;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.Infer;
import net.sourceforge.pmd.lang.java.types.internal.infer.MethodCallSite;
import net.sourceforge.pmd.lang.java.types.internal.infer.PolySite;
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.JavaExprMirrors;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

public class LambdaCanBeMethodReferenceRule extends AbstractJavaRulechainRule {
    // Note that this whole thing is mostly syntactic and does not take care of the details
    // like (boxing) conversions, or when the method ref is ambiguous and the lambda is not.
    // Maybe in a second pass we can check that the overload resolution would succeed
    // with the method reference, similar to what UnnecessaryCastRule is doing.


    private static final PropertyDescriptor<Boolean> IGNORE_IF_MAY_NPE =
        PropertyFactory.booleanProperty("ignoreIfMayNPE")
                       .desc("Ignore lambdas that may throw a null pointer exception (NPE) when converted to a method reference. "
                           + "Those expressions will NPE at mref creation time, while the equivalent lambda would NPE only when invoked (which may be never).")
                       .defaultValue(false)
                       .build();


    private static final PropertyDescriptor<Boolean> IGNORE_IF_RECEIVER_IS_METHOD =
        PropertyFactory.booleanProperty("ignoreIfReceiverIsMethod")
                       .desc("Ignore if the receiver of the method reference is a method call. "
                           + "These may cause side effects that often should prevent the conversion to a method reference.")
                       .defaultValue(true)
                       .build();

    public LambdaCanBeMethodReferenceRule() {
        super(ASTLambdaExpression.class);
        definePropertyDescriptor(IGNORE_IF_MAY_NPE);
        definePropertyDescriptor(IGNORE_IF_RECEIVER_IS_METHOD);
    }

    @Override
    public Object visit(ASTLambdaExpression node, Object data) {
        if (node.isExpressionBody()) {
            ASTExpression expression = node.getExpressionBody();
            processLambdaWithBody(node, asCtx(data), expression);
        } else {
            ASTStatement onlyStmt = ASTList.singleOrNull(node.getBlockBody());
            if (onlyStmt instanceof ASTReturnStatement) {
                processLambdaWithBody(node, asCtx(data), ((ASTReturnStatement) onlyStmt).getExpr());
            }
        }
        return null;
    }

    private void processLambdaWithBody(ASTLambdaExpression lambda, RuleContext data, ASTExpression expression) {
        if (lambda.getParameters().toStream().any(it -> it.getDeclaredAnnotations().nonEmpty())) {
            return;
        }
        if (expression instanceof ASTMethodCall) {
            ASTMethodCall call = (ASTMethodCall) expression;
            if (canBeTransformed(lambda, call)
                && argumentsListMatches(call, lambda.getParameters())
                && inferenceSucceedsWithMethodRef(lambda, call)) {
                data.addViolation(lambda, buildMethodRefString(lambda, call));
            }
        }
    }

    private String buildMethodRefString(ASTLambdaExpression lambda, ASTMethodCall call) {
        StringBuilder sb = new StringBuilder();
        ASTExpression qualifier = call.getQualifier();
        OverloadSelectionResult info = call.getOverloadSelectionInfo();
        assert !info.isFailed() : "should not be failed: " + call;

        JTypeMirror methodSource = info.getMethodType().getDeclaringType();
        JTypeDeclSymbol classSym = methodSource.getSymbol();
        assert classSym != null
            : "null symbol for " + methodSource + ", method " + info.getMethodType();
        if (qualifier == null && info.getMethodType().isStatic()
            || lambda.getParameters().size() != call.getArguments().size()) {
            // this second condition corresponds to the case the first lambda
            // param is the receiver of the method call
            sb.append(classSym.getSimpleName());
        } else if (qualifier == null) {
            // non-static method with null qualifier
            ASTTypeDeclaration enclosing = call.getEnclosingType();
            JClassType receiver = TypeOps.getReceiverType(enclosing.getTypeMirror(), (JClassSymbol) classSym);
            if (receiver != null && !receiver.getSymbol().equals(enclosing.getSymbol())) {
                // method is declared in an enclosing type and receiver
                // needs qualification
                sb.append(receiver.getSymbol().getSimpleName()).append(".this");
            } else {
                sb.append("this");
            }
        } else {
            boolean needsParentheses = !(qualifier instanceof ASTPrimaryExpression);
            if (needsParentheses) {
                sb.append('(');
            }
            sb.append(PrettyPrintingUtil.prettyPrint(qualifier));
            if (needsParentheses) {
                sb.append(')');
            }
        }
        sb.append("::").append(call.getMethodName());
        return sb.toString();
    }

    private boolean argumentsListMatches(ASTMethodCall call, ASTLambdaParameterList params) {
        ASTArgumentList args = call.getArguments();
        int start;
        if (params.size() == args.size() + 1) {
            // first parameter for the method call may be its receiver
            start = 1;
            JVariableSymbol firstParam = params.get(0).getVarId().getSymbol();
            if (!JavaAstUtils.isReferenceToVar(call.getQualifier(), firstParam)) {
                return false;
            }
        } else if (args.size() == params.size()) {
            start = 0;
        } else {
            return false;
        }

        for (int i = 0; i < args.size(); i++) {
            ASTExpression arg = args.get(i);
            ASTLambdaParameter parm = params.get(i + start);
            if (!JavaAstUtils.isReferenceToVar(arg, parm.getVarId().getSymbol())) {
                return false;
            }
        }
        return true;
    }

    // coarse check to filter out some stuff before checking call arguments
    private boolean canBeTransformed(ASTLambdaExpression lambda, ASTMethodCall call) {
        ASTExpression qualifier = JavaAstUtils.peelCasts(call.getQualifier());
        if (call.getOverloadSelectionInfo().isFailed()) {
            // err on the side of FNs
            return false;
        }
        if (qualifier instanceof ASTConstructorCall) {
            // ctors may not be transformed because that would change semantics,
            // with only one instance being created
            return false;
        } else if (qualifier instanceof ASTTypeExpression
            || qualifier instanceof ASTSuperExpression
            || qualifier instanceof ASTThisExpression
            || qualifier instanceof ASTClassLiteral
            || qualifier instanceof ASTLiteral
            || qualifier == null) {
            // these are always transformable
            return true;
        }

        boolean isIgnoredBecauseOfMethodCall =
            qualifier instanceof ASTMethodCall && getProperty(IGNORE_IF_RECEIVER_IS_METHOD);

        // if call uses first lambda parm as receiver, then the mref may not npe at creation time 
        boolean mayNPE = lambda.getParameters().size() == call.getArguments().size();
        boolean isIgnoredBecauseOfNPE = mayNPE && getProperty(IGNORE_IF_MAY_NPE);
        return !isIgnoredBecauseOfNPE && !isIgnoredBecauseOfMethodCall;

    }


    /**
     * This creates a fake method ref mirror that simulates how the method
     * reference would behave after applying the fix. We redo inference of
     * the context of that method ref (ie, maybe inferring an enclosing method
     * call) and check that inference gives the same result as the current lambda.
     * This ensures that changing the lambda to a method reference does not
     * error with an ambiguity error, and also does not change the result of
     * overload resolution. Refer to e.g. {@link UseDiamondOperatorRule} for a
     * similar approach to checking the validity of a code transformation.
     */
    private static boolean inferenceSucceedsWithMethodRef(ASTLambdaExpression lambda, ASTMethodCall call) {
        ExprContext context = lambda.getConversionContext();
        if (context.isMissing()) {
            return false;
        }

        Infer infer = InternalApiBridge.getInferenceEntryPoint(lambda);
        // this may not mutate the AST
        JavaExprMirrors factory = JavaExprMirrors.forObservation(infer);

        InvocationNode invocContext = InternalApiBridge.getTopLevelExprContext(lambda).getInvocNodeIfInvocContext();
        if (invocContext == null) {
            ExprMirror.LambdaExprMirror lambdaMirror = (ExprMirror.LambdaExprMirror) factory.getTopLevelFunctionalMirror(lambda);
            LambdaAsMethodRefMirror mirror;
            mirror = new LambdaAsMethodRefMirror(lambdaMirror, call);
            // topmostContext = lambda.getConversionContext();
            PolySite<ExprMirror.FunctionalExprMirror> site = infer.newFunctionalSite(mirror, lambda.getConversionContext().getTargetType());
            infer.inferFunctionalExprInUnambiguousContext(site);
            return mirror.succeeded();
        } else {
            ExprMirror.InvocationMirror topMostMirror = factory.getInvocationMirror(invocContext, (e, parent, self) -> {
                ExprMirror defaultImpl = factory.defaultMirrorMaker().createMirrorForSubexpression(e, parent, self);
                if (e == lambda) {
                    return new LambdaAsMethodRefMirror((ExprMirror.LambdaExprMirror) defaultImpl, call);
                } else {
                    return defaultImpl;
                }
            });

            ExprContext topmostContext;
            if (invocContext instanceof ASTExpression) {
                topmostContext = ((ASTExpression) invocContext).getConversionContext();
            } else {
                topmostContext = ExprContext.getMissingInstance();
            }
            JTypeMirror targetType = topmostContext.getPolyTargetType(false);
            MethodCallSite fakeCallSite = infer.newCallSite(topMostMirror, targetType);
            infer.inferInvocationRecursively(fakeCallSite);
            return topMostMirror.isEquivalentToUnderlyingAst()
                   && topmostContext.acceptsType(topMostMirror.getInferredType());
        }
    }


    static final class LambdaAsMethodRefMirror implements ExprMirror.MethodRefMirror {

        LambdaExprMirror lambda;
        private final ASTMethodCall call;
        private final JTypeMirror lhsType;
        private final boolean isLhsType;

        LambdaAsMethodRefMirror(LambdaExprMirror lambda, ASTMethodCall call) {
            this.lambda = lambda;
            this.call = call;
            this.isLhsType = call.getQualifier() == null && call.getMethodType().isStatic()
                             || call.getArguments().size() != lambda.getParamCount()
                             || call.getQualifier() instanceof ASTTypeExpression;

            this.lhsType = call.getQualifier() == null ? call.getMethodType().getDeclaringType()
                                                       : call.getQualifier().getTypeMirror();
        }

        @Override
        public CharSequence getLocationText() {
            return this + " (mref adapter on lambda)";
        }

        @Override
        public String toString() {
            if (isLhsType) {
                return lhsType.toString() + "::" + getMethodName();
            } else {
                return call.getQualifier() + "::" + getMethodName();
            }
        }

        @Override
        public boolean isConstructorRef() {
            return false;
        }

        @Override
        public JTypeMirror getTypeToSearch() {
            return lhsType;
        }

        @Override
        public @Nullable JTypeMirror getLhsIfType() {
            return isLhsType ? lhsType : null;
        }

        @Override
        public String getMethodName() {
            return call.getMethodName();
        }

        @Override
        public @NonNull List<JTypeMirror> getExplicitTypeArguments() {
            return Collections.emptyList();
        }

        InvocationMirror.MethodCtDecl ctDecl;

        @Override
        public void setCompileTimeDecl(InvocationMirror.MethodCtDecl methodType) {
            this.ctDecl = methodType;
        }

        private JMethodSig exactMethod;

        @Override
        public @Nullable JMethodSig getCachedExactMethod() {
            return exactMethod;
        }

        @Override
        public void setCachedExactMethod(@Nullable JMethodSig sig) {
            exactMethod = sig;
        }

        @Override
        public JavaNode getLocation() {
            return lambda.getLocation();
        }

        @Override
        public void setInferredType(@Nullable JTypeMirror mirror) {

        }

        @Override
        public @Nullable JTypeMirror getInferredType() {
            return null;
        }

        @Override
        public TypingContext getTypingContext() {
            return lambda.getTypingContext();
        }

        @Override
        public boolean isEquivalentToUnderlyingAst() {
            return succeeded() && ctDecl.getMethodType().equals(call.getMethodType());
        }

        @Override
        public void setFunctionalMethod(@Nullable JMethodSig methodType) {

        }

        @Override
        public void finishFailedInference(@Nullable JTypeMirror targetType) {
            ctDecl = null;
        }

        @Override
        public @NonNull JClassType getEnclosingType() {
            return lambda.getEnclosingType();
        }

        boolean succeeded() {
            return ctDecl != null && !ctDecl.isFailed();
        }
    }
}
