/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEmptyStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Detects conditional expressions and statements where both branches are equal.
 * @since 7.18.0
 */
public class IdenticalConditionalBranchesRule extends AbstractJavaRulechainRule {

    public IdenticalConditionalBranchesRule() {
        super(ASTIfStatement.class, ASTConditionalExpression.class);
    }

    @Override
    public RuleContext visit(ASTIfStatement node, RuleContext data) {
        if (node.getElseBranch() != null
                && areEquivalent(normalize(node.getThenBranch()),
                    normalize(node.getElseBranch()))) {
            data.addViolation(node);
        }
        return data;
    }

    private boolean areEquivalent(JavaNode thenBranch, JavaNode elseBranch) {
        JavaNode normalizedThen = normalize(thenBranch);
        JavaNode normalizedElse = normalize(elseBranch);
        return normalizedElse == null && normalizedThen == null
            || (normalizedElse != null && normalizedThen != null
                && JavaAstUtils.tokenEquals(normalizedElse, normalizedThen));
    }

    private JavaNode normalize(JavaNode firstBranch) {
        JavaNode first = firstBranch;
        while (first instanceof ASTBlock && ((ASTBlock) first).size() <= 1) {
            first = first.getFirstChild();
        }
        return first instanceof ASTEmptyStatement ? null : first;
    }

    @Override
    public RuleContext visit(ASTConditionalExpression node, RuleContext data) {
        if (node.getElseBranch() != null
            && JavaAstUtils.tokenEquals(node.getThenBranch(), node.getElseBranch())) {
            data.addViolation(node);
        }
        return data;
    }
}
