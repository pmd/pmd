/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

import apex.jorje.semantic.symbol.type.ModifierTypeInfos;

public class AvoidGlobalModifierRule extends AbstractApexRule {

    public AvoidGlobalModifierRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Style");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        return checkForGlobal(node, data);
    }

    @Override
    public Object visit(ASTUserInterface node, Object data) {
        return checkForGlobal(node, data);
    }

    private Object checkForGlobal(ApexNode<?> node, Object data) {
        ASTModifierNode modifierNode = node.getFirstChildOfType(ASTModifierNode.class);

        if (isGlobal(modifierNode) && !hasRestAnnotation(modifierNode) && !hasWebServices(node)) {
            addViolation(data, node);
        }

        return data;
    }

    private boolean hasWebServices(ApexNode<?> node) {
        List<ASTMethod> methods = node.findChildrenOfType(ASTMethod.class);
        for (ASTMethod method : methods) {
            ASTModifierNode methodModifier = method.getFirstChildOfType(ASTModifierNode.class);
            if (isWebService(methodModifier)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWebService(ASTModifierNode modifierNode) {
        return modifierNode != null && modifierNode.getNode().getModifiers().has(ModifierTypeInfos.WEB_SERVICE);
    }

    private boolean isGlobal(ASTModifierNode modifierNode) {
        return modifierNode != null && modifierNode.getNode().getModifiers().has(ModifierTypeInfos.GLOBAL);
    }

    private boolean hasRestAnnotation(ASTModifierNode modifierNode) {
        List<ASTAnnotation> annotations = modifierNode.findChildrenOfType(ASTAnnotation.class);
        for (ASTAnnotation annotation : annotations) {
            if (annotation.hasImageEqualTo("RestResource")) {
                return true;
            }
        }
        return false;
    }
}
