/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.sql.ResultSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
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
    public RuleContext visit(ASTWhileStatement node, RuleContext data) {
        return data;
    }

    @Override
    public RuleContext visit(ASTReturnStatement node, RuleContext data) {
        return data;
    }

    @Override
    public RuleContext visit(ASTIfStatement node, RuleContext data) {
        return data;
    }

    @Override
    public RuleContext visit(ASTMethodCall node, RuleContext data) {
        if (isResultSetMethod(node) && !isCheckedIndirectly(node)) {
            data.addViolation(node);
        }
        return super.visit(node, data);
    }

    private boolean isResultSetMethod(ASTMethodCall node) {
        return METHODS.contains(node.getMethodName())
            && TypeTestUtil.isDeclaredInClass(ResultSet.class, node.getMethodType());
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
