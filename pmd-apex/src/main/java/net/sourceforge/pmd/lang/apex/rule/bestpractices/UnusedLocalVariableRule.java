/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class UnusedLocalVariableRule extends AbstractApexRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTVariableDeclaration.class);
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
            if (usage.getParent() == node) {
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
