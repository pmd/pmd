/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.internal.StablePathMatcher;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

public class IfStatementCanBeSwitchRule extends AbstractJavaRule {

    private static final PropertyDescriptor<Integer> MAX_IF_STATEMENTS_DESCRIPTOR
            = PropertyFactory.intProperty("minimumBranches")
            .desc("Minimal count of switch branches")
            .require(positive()).defaultValue(3).build();

    private static final InvocationMatcher EQUALS_METHOD = InvocationMatcher.parse("_#equals(java.lang.Object)");
    private int countLimit;

    public IfStatementCanBeSwitchRule() {
        definePropertyDescriptor(MAX_IF_STATEMENTS_DESCRIPTOR);
    }

    @Override
    public void start(RuleContext context) {
        countLimit = getProperty(MAX_IF_STATEMENTS_DESCRIPTOR);
        super.start(context);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        // if we already checked this if-else chain, don't check again
        if (node.getParent() instanceof ASTIfStatement && ((ASTIfStatement) node.getParent()).getElseBranch() == node) {
            return super.visit(node, data);
        }
        RuleContext ctx = asCtx(data);
        StablePathMatcher checkSubject = StablePathMatcher.matching(extractCheckSubject(node));
        if (checkSubject != null) {
            ASTStatement elseBranch = node.getElseBranch();
            if (elseBranch instanceof ASTIfStatement) {
                checkIfStatement((ASTIfStatement) elseBranch, checkSubject, node, countLimit - 1, ctx);
            }
        }
        return super.visit(node, data);
    }

    private ASTExpression extractCheckSubject(ASTIfStatement node) {
        ASTExpression checkSubject = null;
        ASTExpression condition = node.getCondition();
        if (condition instanceof ASTInfixExpression && ((ASTInfixExpression) condition).getOperator() == BinaryOp.EQ) {
            ASTExpression left = ((ASTInfixExpression) condition).getLeftOperand();
            ASTExpression right = ((ASTInfixExpression) condition).getRightOperand();
            if (isConstantOrEnum(left)) {
                checkSubject = right;
            } else if (isConstantOrEnum(right)) {
                checkSubject = left;
            }
        }
        if (condition instanceof ASTInfixExpression && ((ASTInfixExpression) condition).getOperator() == BinaryOp.INSTANCEOF) {
            checkSubject = ((ASTInfixExpression) condition).getLeftOperand();
        }
        if (condition instanceof ASTMethodCall && EQUALS_METHOD.matchesCall(condition)) {
            ASTExpression left = ((ASTMethodCall) condition).getQualifier();
            ASTExpression right = ((ASTMethodCall) condition).getArguments().get(0);
            if (left != null && isConstantOrEnum(left)) {
                checkSubject = right;
            } else if (isConstantOrEnum(right)) {
                checkSubject = left;
            }
        }
        return checkSubject;
    }

    private boolean isConstantOrEnum(ASTExpression expression) {
        return expression.isCompileTimeConstant()
                || (expression instanceof ASTFieldAccess && isEnumValue((ASTFieldAccess) expression))

                || (expression instanceof ASTVariableAccess && isEnumValue((ASTVariableAccess) expression));
    }

    private boolean isEnumValue(ASTAssignableExpr.ASTNamedReferenceExpr expression) {
        JTypeMirror declaringType = expression.getSignature().getDeclaringType();
        return TypeTestUtil.isA("java.lang.Enum", declaringType);
    }

    private void checkIfStatement(ASTIfStatement ifStatement, StablePathMatcher parentSubject, ASTIfStatement root,
                                  int limit, RuleContext ctx) {
        ASTExpression checkSubject = extractCheckSubject(ifStatement);
        if (!parentSubject.matches(checkSubject)) {
            return;
        }
        if (limit == 1) {
            ctx.addViolation(root);
            return;
        }
        if (ifStatement.getElseBranch() instanceof ASTIfStatement) {
            checkIfStatement((ASTIfStatement) ifStatement.getElseBranch(), parentSubject, root, limit - 1, ctx);
        }
    }

}
