package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTClassLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaParameterList;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTSuperExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThisExpression;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class LambdaCanBeMethodReferenceRule extends AbstractJavaRulechainRule {

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
            // Note: we on purpose do not even try on block-bodied lambdas.
            // Single-expression block bodies should be reported by another
            // rule for transformation to an expression-body.
            ASTExpression expression = node.getExpression();
            if (expression instanceof ASTMethodCall) {
                ASTMethodCall call = (ASTMethodCall) expression;
                if (canBeTransformed(call) && argumentsListMatches(call, node.getParameters())) {
                    asCtx(data).addViolation(node, buildMethodRefString(call));
                }
            }
        }
        return null;
    }

    private String buildMethodRefString(ASTMethodCall call) {
        StringBuilder sb = new StringBuilder();
        ASTExpression qualifier = call.getQualifier();
        if (qualifier == null) {
            OverloadSelectionResult info = call.getOverloadSelectionInfo();
            assert !info.isFailed() : "should not be failed: " + call;
            boolean isStatic = info.getMethodType().isStatic();
            if (isStatic) {
                JTypeDeclSymbol symbol = info.getMethodType().getDeclaringType().getSymbol();
                assert symbol != null
                    : "null symbol for " + info.getMethodType().getDeclaringType() + ", method " + info.getMethodType();
                sb.append(symbol.getSimpleName());
            } else {
                sb.append("this");
            }
        } else {
            sb.append(PrettyPrintingUtil.prettyPrint(qualifier));
        }
        sb.append("::").append(call.getMethodName());
        return sb.toString();
    }

    private boolean argumentsListMatches(ASTMethodCall call, ASTLambdaParameterList params) {
        ASTArgumentList args = call.getArguments();
        if (args.size() != params.size()) {
            return false;
        }
        for (int i = 0; i < args.size(); i++) {
            ASTExpression arg = args.get(i);
            ASTLambdaParameter parm = params.get(i);
            if (!JavaAstUtils.isReferenceToVar(arg, parm.getVarId().getSymbol())) {
                return false;
            }
        }
        return true;
    }

    private boolean canBeTransformed(ASTMethodCall call) {
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

        return getProperty(REPORT_IF_MAY_NPE);
    }

}
