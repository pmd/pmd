/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTCompactConstructorDeclaration;
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
        // Note that local class declaration statements could be ignored
        // because they don't contribute anything to control flow. But this
        // is rare enough that this has not been implemented. A corresponding
        // test is in the test file.

        JavaNode parent = it.getParent();
        if (JavaRuleUtil.isLastChild(it)) {
            if (parent instanceof ASTSwitchArrowBranch) {
                return !isBranchOfSwitchExpr((ASTSwitchBranch) parent);
            } else if (parent instanceof ASTSwitchFallthroughBranch) {
                return JavaRuleUtil.isLastChild(parent) && !isBranchOfSwitchExpr((ASTSwitchBranch) parent);
            } else {
                return !(parent instanceof ASTLoopStatement); // returns break the loop so are not unnecessary (though it could be replaced by break)
            }
        }

        // so we're not the last child...
        return parent instanceof ASTIfStatement  // maybe we're before the else clause
            || parent instanceof ASTTryStatement; // maybe we're the body of a try
            // also maybe we're the body of a do/while, but that is a loop, so it's necessary
    }

    private static boolean isBranchOfSwitchExpr(ASTSwitchBranch branch) {
        return branch.getParent() instanceof ASTSwitchExpression;
    }

}
