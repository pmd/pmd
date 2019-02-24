/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexUnitTestRule;

import apex.jorje.semantic.ast.modifier.Annotation;
import apex.jorje.semantic.ast.modifier.ModifierOrAnnotation;
import apex.jorje.semantic.symbol.type.AnnotationTypeInfos;
import apex.jorje.semantic.symbol.type.ModifierOrAnnotationTypeInfo;
import apex.jorje.semantic.symbol.type.TypeInfoEquivalence;
import apex.jorje.services.Version;

public class ApexUnitTestMethodShouldHaveIsTestAnnotationRule extends AbstractApexUnitTestRule {
    private static final String TEST = "test";

    @Override
    public Object visit(final ASTUserClass node, final Object data) {
        // test methods should have @isTest annotation.
        final Version classApiVersion = node.getNode().getDefiningType().getCodeUnitDetails().getVersion();

        if (!isTestMethodOrClass(node) && classApiVersion.isGreaterThan(Version.V174)) {
            return data;
        }

        checkForIsTestAnnotation(node, data);
        return super.visit(node, data);
    }

    private Object checkForIsTestAnnotation(final ApexNode<?> node, final Object data) {
        final List<ASTMethod> methods = node.findDescendantsOfType(ASTMethod.class);
        final List<ASTMethod> testMethods = new ArrayList<>();
        for (ASTMethod method : methods) {
            final Version classApiVersion = method.getNode().getDefiningType().getCodeUnitDetails().getVersion();
            if (!isTestMethodOrClass(method) && classApiVersion.isGreaterThan(Version.V174)
                    && !method.getImage().toLowerCase(Locale.ROOT).contains(TEST)) {
                continue;
            }
            testMethods.add(method);
        }
        Map<Integer, ASTMethod> methodLocMap = new HashMap<>();
        for (ASTMethod testMethod : testMethods) {
            methodLocMap.put(testMethod.getNode().getLoc().getLine(), testMethod);
        }
        List<ASTModifierNode> modifierList = node.findDescendantsOfType(ASTModifierNode.class);
        final Map<Integer, ModifierOrAnnotation> modifierLocMap = new HashMap<>();
        for (ASTModifierNode modifier : modifierList) {
            for (final ModifierOrAnnotationTypeInfo modifierOrAnnotationTypeInfo : modifier.getNode().getModifiers().all()) {
                ModifierOrAnnotation modifierOrAnnotation = modifier.getNode().getModifiers().get(modifierOrAnnotationTypeInfo);
                if (modifierOrAnnotation instanceof Annotation && TypeInfoEquivalence
                        .isEquivalent(modifierOrAnnotationTypeInfo, AnnotationTypeInfos.IS_TEST)) {
                    modifierLocMap.put(modifierOrAnnotation.getLoc().getLine(), modifierOrAnnotation);
                }
            }
        }
        for (Map.Entry<Integer, ASTMethod> entry : methodLocMap.entrySet()) {
            if (entry != null && modifierLocMap.get(entry.getKey()) == null
                    && modifierLocMap.get(entry.getKey() - 1) == null) {
                addViolationWithMessage(data, node,
                        "''{0}'' method should have @isTest annotation.",
                        new Object[] { entry.getValue().getImage() });
            }
        }
        return data;
    }
}
