/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.sql.ResultSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.ReturnScopeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Rule that verifies, that the return values of next(), first(), last(), etc.
 * calls to a java.sql.ResultSet are actually verified.
 */
public class CheckResultSetRule extends AbstractJavaRule {

    private static final Set<String> METHODS = setOf("next", "previous", "last", "first");

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        // Keep skipping checked navigation conditions and their bodies to avoid existing false positives.
        if (node.getCondition().descendantsOrSelf().filterIs(ASTMethodCall.class).any(this::isResultSetMethod)) {
            return null;
        }
        // A cached navigation result used as the loop condition also checks the navigation call.
        if (isCheckedNavigationVariable(node.getCondition())) {
            return null;
        }
        return super.visit(node, data);
    }

    private boolean isCheckedNavigationVariable(ASTExpression condition) {
        return condition.descendantsOrSelf().filterIs(ASTAssignableExpr.ASTNamedReferenceExpr.class).any(reference -> {
            if (reference.getReferencedSym() == null) {
                return false;
            }
            ASTVariableId variableId = reference.getReferencedSym().tryGetNode();
            if (variableId == null) {
                return false;
            }
            ASTExpression initializer = variableId.getInitializer();
            return initializer != null
                && initializer.descendantsOrSelf().filterIs(ASTMethodCall.class).any(this::isResultSetMethod);
        });
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        // Keep skipping checked navigation conditions and their bodies to avoid existing false positives.
        if (node.getCondition().descendantsOrSelf().filterIs(ASTMethodCall.class).any(this::isResultSetMethod)) {
            return null;
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (isResultSetMethod(node) && !isInConditionOfTernary(node) && !isCheckedIndirectly(node)) {
            ctx.addViolation(node);
        }
        return super.visit(node, data);
    }

    private boolean isResultSetMethod(ASTMethodCall node) {
        return METHODS.contains(node.getMethodName())
            && TypeTestUtil.isDeclaredInClass(ResultSet.class, node.getMethodType());
    }

    private boolean isInConditionOfTernary(ASTMethodCall node) {
        JavaNode child = node;
        for (JavaNode parent : node.ancestors().takeWhile(n -> !(n instanceof ReturnScopeNode))) {
            if (parent instanceof ASTConditionalExpression
                && ((ASTConditionalExpression) parent).getCondition() == child) {
                return true;
            }
            child = parent;
        }
        return false;
    }

    private boolean isCheckedIndirectly(ASTMethodCall node) {
        final ASTVariableDeclarator variableDeclarator = node.ancestors()
                .takeWhile(n -> !(n instanceof ReturnScopeNode))
                .first(ASTVariableDeclarator.class);

        if (variableDeclarator == null) {
            return false;
        }

        final List<ASTAssignableExpr.ASTNamedReferenceExpr> usages = variableDeclarator.getVarId().getLocalUsages();
        //check that the result is used and its first usage is not overwriting the result
        return !usages.isEmpty() && usages.get(0).getAccessType() == ASTAssignableExpr.AccessType.READ;
    }
}
