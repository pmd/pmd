/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotationParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;

/**
 * <p>
 * It's a very bad practice to use @isTest(seeAllData=true) in Apex unit tests,
 * because it opens up the existing database data for unexpected modification by
 * tests.
 * </p>
 *
 * @author a.subramanian
 */
public class ApexUnitTestShouldNotUseSeeAllDataTrueRule extends AbstractApexUnitTestRule {

    @Override
    public Object visit(final ASTUserClass node, final Object data) {
        if (!isTestMethodOrClass(node)) {
            return data;
        }

        checkForSeeAllData(node, data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (!isTestMethodOrClass(node)) {
            return data;
        }

        return checkForSeeAllData(node, data);
    }

    private Object checkForSeeAllData(final ApexNode<?> node, final Object data) {
        final ASTModifierNode modifierNode = node.firstChild(ASTModifierNode.class);

        if (modifierNode != null) {
            for (ASTAnnotationParameter parameter : modifierNode.descendants(ASTAnnotationParameter.class)) {
                if (parameter.hasName(ASTAnnotationParameter.SEE_ALL_DATA) && parameter.getBooleanValue()) {
                    asCtx(data).addViolation(node);
                    return data;
                }
            }
        }

        return data;
    }
}
