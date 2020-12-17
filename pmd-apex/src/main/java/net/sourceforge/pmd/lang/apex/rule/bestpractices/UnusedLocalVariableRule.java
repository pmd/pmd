/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class UnusedLocalVariableRule extends AbstractApexRule {
    public UnusedLocalVariableRule() {
        addRuleChainVisit(ASTVariableDeclaration.class);
    }

    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {
        String variableName = node.getImage();

        ASTBlockStatement variableContext = node.getFirstParentOfType(ASTBlockStatement.class);
        if (variableContext == null) {
            // if there is no parent BlockStatement, e.g. in triggers
            return data;
        }

        List<ApexNode<?>> potentialUsages = new ArrayList<>();

        // Variable expression catch things like the `a` in `a + b`
        potentialUsages.addAll(variableContext.findDescendantsOfType(ASTVariableExpression.class));
        // Reference expressions catch things like the `a` in `a.foo()`
        potentialUsages.addAll(variableContext.findDescendantsOfType(ASTReferenceExpression.class));

        for (ApexNode<?> usage : potentialUsages) {
            if (node.equals(usage.getParent())) {
                continue;
            }

            if (StringUtils.equalsIgnoreCase(variableName, usage.getImage())) {
                return data;
            }
        }

        addViolation(data, node, variableName);
        return data;
    }
}
