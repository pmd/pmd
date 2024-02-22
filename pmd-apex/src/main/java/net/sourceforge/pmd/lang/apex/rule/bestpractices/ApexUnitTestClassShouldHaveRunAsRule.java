/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTRunAsBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;

/**
 * Apex unit tests should have System.runAs methods in them
 *
 * @author t.prouvot
 */
public class ApexUnitTestClassShouldHaveRunAsRule extends AbstractApexUnitTestRule {

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (!isTestMethodOrClass(node)) {
            return data;
        }

        return checkForRunAsStatements(node, data);
    }

    private Object checkForRunAsStatements(ApexNode<?> node, Object data) {
        final List<ASTRunAsBlockStatement> runAsStatements = node.descendants(ASTRunAsBlockStatement.class).toList();

        if (runAsStatements.isEmpty()) {
            asCtx(data).addViolation(node);
        }
        return data;
    }
}
