/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.naming;

import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import static apex.jorje.semantic.symbol.type.ModifierTypeInfos.OVERRIDE;

public class MethodNamingConventionsRule extends AbstractApexRule {

    public Object visit(ASTUserClass node, Object data) {
        return super.visit(node, data);
    }

    public Object visit(ASTMethod node, Object data) {
        if (isOverriddenMethod(node)) {
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
}
