/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.apexunit;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;

import apex.jorje.semantic.ast.modifier.Annotation;
import apex.jorje.semantic.ast.modifier.AnnotationParameter;
import apex.jorje.semantic.ast.modifier.ModifierOrAnnotation;
import apex.jorje.semantic.symbol.type.AnnotationTypeInfos;
import apex.jorje.semantic.symbol.type.TypeInfoEquivalence;
import apex.jorje.services.Version;

/**
 * <p>
 * It's a very bad practice to use @isTest(seeAllData=true) in Apex unit tests,
 * because it opens up the existing database data for unexpected modification by
 * tests.
 * </p>
 *
 * @author a.subramanian
 */
public class ApexUnitTestShouldNotUseSeeAllDataTrue extends AbstractApexUnitTestRule {

    @Override
    public Object visit(final ASTUserClass node, final Object data) {
        // @isTest(seeAllData) was introduced in v24, and was set to false by
        // default
        final Version classApiVersion = node.getNode().getDefiningType().getCodeUnitDetails().getVersion();

        if (!isTestMethodOrClass(node) && classApiVersion.isGreaterThan(Version.V174)) {
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
        final ASTModifierNode modifierNode = node.getFirstChildOfType(ASTModifierNode.class);

        if (modifierNode != null) {
            for (final ModifierOrAnnotation modifierOrAnnotation : modifierNode.getNode().getModifiers().allNodes()) {
                if (modifierOrAnnotation instanceof Annotation && TypeInfoEquivalence
                        .isEquivalent(modifierOrAnnotation.getType(), AnnotationTypeInfos.IS_TEST)) {
                    final Annotation annotation = (Annotation) modifierOrAnnotation;
                    final AnnotationParameter parameter = annotation.getParameter("seeAllData");

                    if (parameter != null && parameter.getBooleanValue() == true) {
                        addViolation(data, node);
                        return data;
                    }
                }
            }
        }

        return data;
    }
}
