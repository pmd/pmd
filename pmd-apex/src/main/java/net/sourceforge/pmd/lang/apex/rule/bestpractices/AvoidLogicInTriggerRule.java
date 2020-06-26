/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class AvoidLogicInTriggerRule extends AbstractApexRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserTrigger.class);
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
