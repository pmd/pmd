/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotationParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;

/**
 * Apex unit test classes should declare either the {@code critical} or the {@code testFor} modifier
 * of the {@code @IsTest} annotation, so that their relevance for a {@code RunRelevantTests} deployment
 * is explicit rather than left entirely to Salesforce's automatic dependency detection.
 */
public class ApexUnitTestClassShouldHaveRunRelevantTestsAnnotationRule extends AbstractApexUnitTestRule {

    private static final double MIN_API_VERSION = 66.0;

    @Override
    public Object visit(final ASTUserClass node, final Object data) {
        if (!isTestMethodOrClass(node)) {
            return data;
        }

        checkForRunRelevantTestsAnnotation(node, data);
        return super.visit(node, data);
    }

    private void checkForRunRelevantTestsAnnotation(final ASTUserClass node, final Object data) {
        // the 'critical'/'testFor' modifiers are only available starting with API v66.0; if the
        // class's api version is known and older than that, it can't use them, so don't flag it
        if (node.getRoot().getApiVersion().map(version -> version < MIN_API_VERSION).orElse(false)) {
            return;
        }

        final ASTModifierNode modifierNode = node.firstChild(ASTModifierNode.class);

        if (modifierNode != null) {
            for (ASTAnnotationParameter parameter : modifierNode.descendants(ASTAnnotationParameter.class)) {
                if (parameter.hasName(ASTAnnotationParameter.CRITICAL) && parameter.getBooleanValue()) {
                    return;
                }
                if (parameter.hasName(ASTAnnotationParameter.TEST_FOR)
                        && parameter.getValue() != null && !parameter.getValue().isEmpty()) {
                    return;
                }
            }
        }

        asCtx(data).addViolation(node);
    }
}
