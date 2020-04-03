/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class UnusedLocalVariableRule extends AbstractApexRule {
    public UnusedLocalVariableRule() {
        addRuleChainVisit(ASTVariableDeclaration.class);
    }

    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {
        String variableName = node.getImage();

        ASTBlockStatement variableContext = node.getFirstParentOfType(ASTBlockStatement.class);
        List<ASTVariableExpression> potentialUsages = variableContext.findDescendantsOfType(ASTVariableExpression.class);

        for (ASTVariableExpression usage : potentialUsages) {
            if (usage.getParent() == node) {
                continue;
            }
            if (usage.getImage().equals(variableName)) {
                return data;
            }
        }

        addViolation(data, node, variableName);
        return data;
    }
}
