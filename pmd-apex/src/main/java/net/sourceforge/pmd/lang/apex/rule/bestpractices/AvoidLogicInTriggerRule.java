/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.List;
import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import org.checkerframework.checker.nullness.qual.NonNull;

public class AvoidLogicInTriggerRule extends AbstractApexRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserTrigger.class);
    }

    @Override
    public Object visit(ASTUserTrigger node, Object data) {
        List<ASTBlockStatement> blockStatements =
                node.descendants(ASTBlockStatement.class).toList();

        if (!blockStatements.isEmpty()) {
            asCtx(data).addViolation(node);
        }

        return data;
    }
}
