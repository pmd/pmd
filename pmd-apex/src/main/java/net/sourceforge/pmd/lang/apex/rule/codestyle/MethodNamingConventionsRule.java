/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import static apex.jorje.semantic.symbol.type.ModifierTypeInfos.OVERRIDE;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class MethodNamingConventionsRule extends AbstractApexRule {

    public MethodNamingConventionsRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Style");
        // Note: x10 as Apex has not automatic refactoring
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 1);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (isOverriddenMethod(node) || isPropertyAccessor(node) || isConstructor(node)) {
            return data;
        }

        String methodName = node.getImage();

        if (Character.isUpperCase(methodName.charAt(0))) {
            addViolationWithMessage(data, node, "Method names should not start with capital letters");
        }
        if (methodName.indexOf('_') >= 0) {
            addViolationWithMessage(data, node, "Method names should not contain underscores");
        }
        return data;
    }

    private boolean isOverriddenMethod(ASTMethod node) {
        return node.getNode().getModifiers().has(OVERRIDE);
    }

    private boolean isPropertyAccessor(ASTMethod node) {
        return !node.getParentsOfType(ASTProperty.class).isEmpty();
    }

    private boolean isConstructor(ASTMethod node) {
        return node.getNode().getMethodInfo().isConstructor();
    }
}
