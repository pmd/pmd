/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class AvoidLogicInTriggerRule extends AbstractApexRule {

    public AvoidLogicInTriggerRule() {
        addRuleChainVisit(ASTUserTrigger.class);
    }

    @Override
    public Object visit(ASTUserTrigger node, Object data) {
        List<ASTBlockStatement> blockStatements = node.findDescendantsOfType(ASTBlockStatement.class);

        if (!blockStatements.isEmpty()) {
            addViolation(data, node);
        }

        return data;
    }
}
