/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyClause;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

public class UnnecessaryReturnRule extends AbstractJavaRulechainRule {

    public UnnecessaryReturnRule() {
        super(ASTReturnStatement.class);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        if (node.getNumChildren() > 0) {
            return null;
        } else if (node.ancestors()
                       .takeWhile(it -> !(it instanceof ASTBodyDeclaration || it instanceof ASTLambdaExpression))
                       .filterIs(ASTStatement.class)
                       .all(this::isLastStatementOfParent)) {
            addViolation(data, node);
        }
        return null;
    }

    /**
     * Returns true if this is the last statement of the parent node,
     * ie the next statement to be executed is some sibling of the parent.
     */
    private boolean isLastStatementOfParent(ASTStatement it) {
        // last child of the parent.
        if (it.getNextSibling() == null) {
            return true;
        }
        JavaNode parent = it.getParent();
        return parent instanceof ASTIfStatement
            || parent instanceof ASTLoopStatement
            // these are for the ASTBlock of these constructs
            || parent instanceof ASTTryStatement
            || parent instanceof ASTFinallyClause
            || parent instanceof ASTCatchClause
            || parent instanceof ASTSynchronizedStatement;
    }

}
