/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotationParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexUnitTestRule;

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
        // @isTest(seeAllData) was introduced in v24, and was set to false by default
        /*
        if (!isTestMethodOrClass(node) && node.getApexVersion() >= Version.V176.getExternal()) {
            return data;
        }
         */
        // TODO(b/239648780)

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
        final ASTModifierNode modifierNode = node.getFirstChildOfType(ASTModifierNode.class);

        if (modifierNode != null) {
            List<ASTAnnotationParameter> annotationParameters = modifierNode.findDescendantsOfType(ASTAnnotationParameter.class);
            for (ASTAnnotationParameter parameter : annotationParameters) {
                // if (ASTAnnotationParameter.SEE_ALL_DATA.equals(parameter.getName()) && parameter.getBooleanValue()) {
                    addViolation(data, node);
                    return data;
                // }
                // TODO(b/239648780)
            }
        }

        return data;
    }
}
