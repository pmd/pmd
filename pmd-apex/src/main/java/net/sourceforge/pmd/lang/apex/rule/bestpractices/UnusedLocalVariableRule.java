package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

import java.util.List;

public class UnusedLocalVariableRule extends AbstractApexRule {
    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {
        String variableName = node.getImage();

        ASTMethod containerMethod = node.getFirstParentOfType(ASTMethod.class);
        List<ASTVariableExpression> potentialUsages = containerMethod.findChildrenOfType(ASTVariableExpression.class);

        for (ASTVariableExpression usage : potentialUsages) {
            if (usage.getImage().equals(variableName)) {
                return data;
            }
        }

        addViolation(data, node);
        return data;
    }
}
