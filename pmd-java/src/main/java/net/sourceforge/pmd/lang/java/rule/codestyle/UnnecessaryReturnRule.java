/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTCompactConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyClause;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;

public class UnnecessaryReturnRule extends AbstractJavaRulechainRule {

    public UnnecessaryReturnRule() {
        super(ASTReturnStatement.class);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        if (node.getNumChildren() > 0) {
            return null;
        }
        NodeStream<ASTStatement> enclosingStatements =
            node.ancestorsOrSelf()
                .takeWhile(it -> !isCfgLimit(it))
                .filterIs(ASTStatement.class);

        if (enclosingStatements.all(UnnecessaryReturnRule::isLastStatementOfParent)) {
            addViolation(data, node);
        }
        return null;
    }

    private boolean isCfgLimit(JavaNode it) {
        return it instanceof ASTMethodOrConstructorDeclaration
            || it instanceof ASTCompactConstructorDeclaration
            || it instanceof ASTInitializer
            || it instanceof ASTLambdaExpression;
    }

    /**
     * Returns true if this is the last statement of the parent node,
     * ie the next statement to be executed is after the parent in the
     * CFG.
     */
    private static boolean isLastStatementOfParent(ASTStatement it) {
        // last child of the parent.
        JavaNode parent = it.getParent();
        if (JavaRuleUtil.isLastChild(it)) {
            if (parent instanceof ASTSwitchArrowBranch) {
                return !isBranchOfSwitchExpr((ASTSwitchBranch) parent);
            } else if (parent instanceof ASTSwitchFallthroughBranch) {
                return JavaRuleUtil.isLastChild(parent) && !isBranchOfSwitchExpr((ASTSwitchBranch) parent);
            }
            return true;
        }

        return parent instanceof ASTIfStatement
            || parent instanceof ASTLoopStatement
            // these are for the ASTBlock of these constructs
            || parent instanceof ASTTryStatement
            || parent instanceof ASTFinallyClause
            || parent instanceof ASTCatchClause
            || parent instanceof ASTSynchronizedStatement
            || parent instanceof ASTSwitchArrowBranch && !isBranchOfSwitchExpr((ASTSwitchBranch) parent);
    }

    private static boolean isBranchOfSwitchExpr(ASTSwitchBranch branch) {
        return branch.getParent() instanceof ASTSwitchExpression;
    }

}
