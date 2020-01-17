/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTSoslExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.Node;

public class AvoidSoslInLoopsRule extends AbstractApexRule {

    public AvoidSoslInLoopsRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Performance");
        // Note: Often more complicated as just moving the SOSL a few lines.
        // Involves Maps...
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 150);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTSoslExpression node, Object data) {
        if (insideLoop(node) && parentNotReturn(node) && parentNotForEach(node)) {
            addViolation(data, node);
        }
        return data;
    }

    private boolean parentNotReturn(ASTSoslExpression node) {
        return !(node.getParent() instanceof ASTReturnStatement);
    }

    private boolean parentNotForEach(ASTSoslExpression node) {
        return !(node.getParent() instanceof ASTForEachStatement);
    }

    private boolean insideLoop(ASTSoslExpression node) {
        Node n = node.getParent();

        while (n != null) {
            if (n instanceof ASTDoLoopStatement || n instanceof ASTWhileLoopStatement
                    || n instanceof ASTForLoopStatement || n instanceof ASTForEachStatement) {
                return true;
            }
            n = n.getParent();
        }

        return false;
    }
}
