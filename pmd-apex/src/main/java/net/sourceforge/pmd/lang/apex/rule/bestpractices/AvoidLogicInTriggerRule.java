/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.reporting.RuleContext;

public class AvoidLogicInTriggerRule extends AbstractApexRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserTrigger.class);
    }

    @Override
    public RuleContext visit(ASTUserTrigger node, RuleContext data) {
        List<ASTBlockStatement> blockStatements = node.descendants(ASTBlockStatement.class).toList();

        if (!blockStatements.isEmpty()) {
            data.addViolation(node);
        }

        return data;
    }
}
