/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Base class for any rules that detect operations contained within a loop that could be more efficiently executed by
 * refactoring the code into a batched execution.
 */
abstract class AbstractAvoidNodeInLoopsRule extends AbstractApexRule {
    /**
     * Adds a violation if any parent of {@code node} is a looping construct that would cause {@code node} to execute
     * multiple times and {@code node} is not part of a return statement that short circuits the loop.
     */
    protected Object checkForViolation(ApexNode<?> node, Object data) {
        if (insideLoop(node) && parentNotReturn(node)) {
            addViolation(data, node);
        }
        return data;
    }

    /**
     * @return false if {@code node} is a direct child of a return statement. Children of return statements should not
     * result in a violation because the return short circuits the loop's execution.
     */
    private boolean parentNotReturn(ApexNode<?> node) {
        return !(node.getParent() instanceof ASTReturnStatement);
    }

    /**
     * @return true if any parent of {@code node} is a construct that would cause {@code node} to execute multiple
     * times.
     */
    private boolean insideLoop(Node node) {
        Node n = node.getParent();

        while (n != null) {
            if (n instanceof ASTBlockStatement && n.getParent() instanceof ASTForEachStatement) {
                // only consider the block of the for-each statement, not the iterator
                return true;
            }
            if (n instanceof ASTDoLoopStatement || n instanceof ASTWhileLoopStatement
                    || n instanceof ASTForLoopStatement) {
                return true;
            }
            n = n.getParent();
        }

        return false;
    }
}
