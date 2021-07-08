/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * In the Summer '21 release, a mandatory security update enforces access
 * modifiers on Apex properties in Lightning component markup. The update
 * prevents access to private or protected Apex getters from Aura and Lightning
 * Web Components.
 * 
 * @author p.ozil
 */
public class InaccessibleAuraEnabledGetterRule extends AbstractApexRule {

    public InaccessibleAuraEnabledGetterRule() {
        addRuleChainVisit(ASTProperty.class);
    }

    @Override
    public Object visit(ASTProperty node, Object data) {
        // Find @AuraEnabled property
        ASTModifierNode propModifiers = node.getModifiers();
        if (hasAuraEnabledAnnotation(propModifiers)) {
            // Find getters/setters if any
            List<ASTMethod> methods = node.findChildrenOfType(ASTMethod.class);
            for (ASTMethod method : methods) {
                // Find getter method
                if (!"void".equals(method.getReturnType())) {
                    // Ensure getter is not private or protected
                    ASTModifierNode methodModifiers = method.getModifiers();
                    if (isPrivate(methodModifiers) || isProtected(methodModifiers)) {
                        addViolation(data, node);
                    }
                }
            }
        }
        return data;
    }

    private boolean isPrivate(ASTModifierNode modifierNode) {
        return modifierNode != null && modifierNode.isPrivate();
    }

    private boolean isProtected(ASTModifierNode modifierNode) {
        return modifierNode != null && modifierNode.isProtected();
    }

    private boolean hasAuraEnabledAnnotation(ASTModifierNode modifierNode) {
        List<ASTAnnotation> annotations = modifierNode.findChildrenOfType(ASTAnnotation.class);
        for (ASTAnnotation annotation : annotations) {
            if (annotation.hasImageEqualTo("AuraEnabled")) {
                return true;
            }
        }
        return false;
    }
}
