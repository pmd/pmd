/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * Flags Apex classes that declare {@code @InvocableVariable} fields or properties but define
 * at least one explicit constructor without also providing an accessible no-argument constructor.
 *
 * <p>Salesforce Flow instantiates invocable-variable classes via a zero-argument constructor.
 * When a developer adds any explicit constructor, Apex no longer synthesises a default
 * one, so Flow cannot create an instance and throws a runtime error. Global classes additionally
 * require the no-arg constructor to be {@code global} so that it is accessible from managed
 * packages.
 */
public class InvocableClassNoArgConstructorRule extends AbstractApexRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserClass.class);
    }

    /**
     * Checks whether the visited class contains {@code @InvocableVariable} members and, if so,
     * whether every explicit constructor is accompanied by an accessible no-argument constructor.
     *
     * @param node the class node being visited
     * @param data the rule context data
     * @return the (unchanged) rule context data
     */
    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (!hasInvocableVariable(node)) {
            return data;
        }

        List<ASTMethod> methods = node.descendants(ASTMethod.class).toList();
        boolean hasCustomConstructors = false;
        boolean hasValidNoArgConstructor = false;

        boolean isClassGlobal = isGlobal(node.firstChild(ASTModifierNode.class));

        for (ASTMethod method : methods) {
            if (method.isConstructor()) {
                hasCustomConstructors = true;

                if (method.getArity() == 0) {
                    ASTModifierNode constructorModifiers = method.firstChild(ASTModifierNode.class);

                    if (isClassGlobal) {
                        // Global classes require a global no-arg constructor for cross-package Flows
                        if (isGlobal(constructorModifiers)) {
                            hasValidNoArgConstructor = true;
                            break;
                        }
                    } else {
                        if (!isPrivate(constructorModifiers)) {
                            hasValidNoArgConstructor = true;
                            break;
                        }
                    }
                }
            }
        }

        if (hasCustomConstructors && !hasValidNoArgConstructor) {
            asCtx(data).addViolation(node, node.getImage());
        }

        return data;
    }

    private boolean hasInvocableVariable(ASTUserClass classNode) {
        for (ASTField field : classNode.descendants(ASTField.class).toList()) {
            if (hasInvocableAnnotation(field.getModifiers())) {
                return true;
            }
        }

        for (ASTProperty property : classNode.descendants(ASTProperty.class).toList()) {
            if (hasInvocableAnnotation(property.getModifiers())) {
                return true;
            }
        }

        return false;
    }

    private boolean hasInvocableAnnotation(ASTModifierNode modifiers) {
        if (modifiers == null) {
            return false;
        }
        for (ASTAnnotation annotation : modifiers.children(ASTAnnotation.class)) {
            if ("InvocableVariable".equalsIgnoreCase(annotation.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isGlobal(ASTModifierNode modifiers) {
        return modifiers != null && modifiers.isGlobal();
    }

    private boolean isPrivate(ASTModifierNode modifiers) {
        return modifiers != null && modifiers.isPrivate();
    }
}
