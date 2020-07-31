/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.Node;

public class AvoidSoqlInLoopsRule extends AbstractApexRule {

    public AvoidSoqlInLoopsRule() {
        addRuleChainVisit(ASTSoqlExpression.class);
        setProperty(CODECLIMATE_CATEGORIES, "Performance");
        // Note: Often more complicated as just moving the SOQL a few lines.
        // Involves Maps...
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 150);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTSoqlExpression node, Object data) {
        if (insideLoop(node) && parentNotReturn(node)) {
            addViolation(data, node);
        }
        return data;
    }

    private boolean parentNotReturn(ASTSoqlExpression node) {
        return !(node.getParent() instanceof ASTReturnStatement);
    }

    private boolean insideLoop(ASTSoqlExpression node) {
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
