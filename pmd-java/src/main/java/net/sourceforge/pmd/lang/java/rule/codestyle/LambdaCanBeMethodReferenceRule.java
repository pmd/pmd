package net.sourceforge.pmd.lang.java.rule.codestyle;

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
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSuperExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThisExpression;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

public class LambdaCanBeMethodReferenceRule extends AbstractJavaRulechainRule {
    // Note that this whole thing is mostly syntactic and does not take care of the details
    // like (boxing) conversions, or when the method ref is ambiguous and the lambda is not.
    // Maybe in a second pass we can check that the overload resolution would succeed
    // with the method reference, similar to what UnnecessaryCastRule is doing.


    private static final PropertyDescriptor<Boolean> REPORT_IF_MAY_NPE =
        PropertyFactory.booleanProperty("reportEvenIfMayNPE")
                       .desc("Also report those expressions that may throw a null pointer exception (NPE) when converted to a method reference. "
                           + "Those expressions will NPE at mref creation time, while the equivalent lambda would NPE only when invoked (which may be never).")
                       .defaultValue(true)
                       .build();

    public LambdaCanBeMethodReferenceRule() {
        super(ASTLambdaExpression.class);
        definePropertyDescriptor(REPORT_IF_MAY_NPE);
    }

    @Override
    public Object visit(ASTLambdaExpression node, Object data) {
        if (node.isExpressionBody()) {
            ASTExpression expression = node.getExpression();
            processLambdaWithBody(node, asCtx(data), expression);
        } else {
            ASTStatement onlyStmt = ASTList.singleOrNull(node.getBlock());
            if (onlyStmt instanceof ASTReturnStatement) {
                processLambdaWithBody(node, asCtx(data), ((ASTReturnStatement) onlyStmt).getExpr());
            }
        }
        return null;
    }

    private void processLambdaWithBody(ASTLambdaExpression lambda, RuleContext data, ASTExpression expression) {
        if (expression instanceof ASTMethodCall) {
            ASTMethodCall call = (ASTMethodCall) expression;
            if (canBeTransformed(lambda, call) && argumentsListMatches(call, lambda.getParameters())) {
                data.addViolation(lambda, buildMethodRefString(lambda, call));
            }
        }
    }

    private String buildMethodRefString(ASTLambdaExpression lambda, ASTMethodCall call) {
        StringBuilder sb = new StringBuilder();
        ASTExpression qualifier = call.getQualifier();
        OverloadSelectionResult info = call.getOverloadSelectionInfo();
        assert !info.isFailed() : "should not be failed: " + call;

        if (qualifier == null && info.getMethodType().isStatic()
            || lambda.getParameters().size() != call.getArguments().size()) {
            // this second condition corresponds to the case the first lambda
            // param is the receiver of the method call

            JTypeDeclSymbol symbol = info.getMethodType().getDeclaringType().getSymbol();
            assert symbol != null
                : "null symbol for " + info.getMethodType().getDeclaringType() + ", method " + info.getMethodType();
            sb.append(symbol.getSimpleName());
        } else if (qualifier == null) {
            sb.append("this");
        } else {
            sb.append(PrettyPrintingUtil.prettyPrint(qualifier));
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
        ASTExpression qualifier = call.getQualifier();
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

        if (lambda.getParameters().size() == call.getArguments().size() + 1) {
            return true;
        }

        return getProperty(REPORT_IF_MAY_NPE);
    }

}
