/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.Collection;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ReturnScopeNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.AssignmentEntry;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.DataflowResult;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.ReachingDefinitionSet;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * For methods that return an array, a {@link Collection} or a {@link Map}, this rule reports
 * a {@code return} statement when the value it produces has an explicit {@code null} source.
 *
 * <p>The analysis is conservative about values it cannot trace: method calls, field reads and
 * parameters are not treated as {@code null} without an explicit {@code null} source, and the
 * {@code null} literal is ignored when it appears only in a condition, a switch selector or an array
 * element rather than in the produced value.
 *
 * @since 7.27.0
 */
public class ReturnEmptyCollectionRatherThanNullRule extends AbstractJavaRulechainRule {

    public ReturnEmptyCollectionRatherThanNullRule() {
        super(ASTReturnStatement.class);
    }

    @Override
    public Object visit(ASTReturnStatement returnStmt, Object data) {
        ReturnScopeNode target = JavaAstUtils.getReturnTarget(returnStmt);
        if (!(target instanceof ASTMethodDeclaration)) {
            return data;
        }

        ASTMethodDeclaration method = (ASTMethodDeclaration) target;
        if (!returnsArrayOrCollection(method)) {
            return data;
        }

        ASTExpression expression = returnStmt.getExpr();
        if (expression == null) {
            return data;
        }

        if (mayYieldExplicitNull(expression) || reachesExplicitNullThroughLocal(expression)) {
            asCtx(data).addViolation(returnStmt);
        }
        return data;
    }

    private static boolean returnsArrayOrCollection(ASTMethodDeclaration method) {
        JTypeMirror returnType = method.getResultTypeNode().getTypeMirror();
        return returnType.isArray()
            || TypeTestUtil.isA(Collection.class, returnType)
            || TypeTestUtil.isA(Map.class, returnType);
    }

    /**
     * Returns true when {@code expression} can produce the value {@code null} through an explicit
     * null source. The condition of a conditional and the selector of a switch are value-selecting
     * expressions, not produced values, so they are not examined. Local variables are not traced
     * here; {@link #reachesExplicitNullThroughLocal(ASTExpression)} handles a bare returned local.
     */
    private static boolean mayYieldExplicitNull(ASTExpression expression) {
        if (expression instanceof ASTNullLiteral) {
            return true;
        } else if (expression instanceof ASTConditionalExpression) {
            ASTConditionalExpression conditional = (ASTConditionalExpression) expression;
            return mayYieldExplicitNull(conditional.getThenBranch())
                || mayYieldExplicitNull(conditional.getElseBranch());
        } else if (expression instanceof ASTCastExpression) {
            return mayYieldExplicitNull(((ASTCastExpression) expression).getOperand());
        } else if (expression instanceof ASTSwitchExpression) {
            for (ASTExpression yielded : ((ASTSwitchExpression) expression).getYieldExpressions()) {
                if (mayYieldExplicitNull(yielded)) {
                    return true;
                }
            }
            return false;
        } else if (expression instanceof ASTAssignmentExpression) {
            ASTAssignmentExpression assignment = (ASTAssignmentExpression) expression;
            // Only a plain assignment can carry an explicit null reference as its value;
            // compound assignments are not a direct null source.
            return !assignment.isCompound()
                && mayYieldExplicitNull(assignment.getRightOperand());
        }
        return false;
    }

    /**
     * For a bare {@code return localVar;}, follows the reaching definitions of the local variable
     * and returns true when any of them has an explicit {@code null} source. This is only applied
     * to a variable returned directly, never to one nested in a conditional or switch branch, so a
     * null guard around the value is not reinterpreted. Reaching definitions that the dataflow
     * analysis cannot fully determine are treated as non-null.
     */
    private static boolean reachesExplicitNullThroughLocal(ASTExpression expression) {
        if (!JavaAstUtils.isReferenceToLocal(expression)) {
            return false;
        }
        ASTVariableAccess access = (ASTVariableAccess) expression;
        DataflowResult dataflow = DataflowPass.getDataflowResult(access.getRoot());
        ReachingDefinitionSet reaching = dataflow.getReachingDefinitions(access);
        if (reaching.isNotFullyKnown()) {
            return false;
        }
        for (AssignmentEntry def : reaching.getReaching()) {
            ASTExpression rhs = def.getRhsAsExpression();
            if (rhs != null && mayYieldExplicitNull(rhs)) {
                return true;
            }
        }
        return false;
    }
}
