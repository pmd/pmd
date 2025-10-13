/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.ReturnScopeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * Rule that verifies, that the return values of next(), first(), last(), etc.
 * calls to a java.sql.ResultSet are actually verified.
 */
public class CheckResultSetRule extends AbstractJavaRule {

    private static final Set<String> METHODS = setOf("next", "previous", "last", "first");

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (isResultSetMethod(node) && !isCheckedIndirectly(node)) {
            asCtx(data).addViolation(node);
        }
        return super.visit(node, data);
    }

    private boolean isResultSetMethod(ASTMethodCall node) {
        return METHODS.contains(node.getMethodName())
            && TypeTestUtil.isDeclaredInClass(ResultSet.class, node.getMethodType());
    }

    private boolean isCheckedIndirectly(ASTMethodCall node) {
        final NodeStream<ASTVariableDeclarator> variableDeclarators = node.ancestors()
                .takeWhile(n -> !(n instanceof ReturnScopeNode))
                .filterIs(ASTVariableDeclarator.class);

        if (variableDeclarators.isEmpty()) {
            return false;
        }

        final List<ASTAssignableExpr.ASTNamedReferenceExpr> usages = variableDeclarators.firstOpt()
                .map(varDecl -> varDecl.getVarId().getLocalUsages())
                .orElse(Collections.emptyList());
        //check that the result is used and its first usage is not overwriting the result
        return !usages.isEmpty() && usages.get(0).getAccessType() == ASTAssignableExpr.AccessType.READ;
    }
}
