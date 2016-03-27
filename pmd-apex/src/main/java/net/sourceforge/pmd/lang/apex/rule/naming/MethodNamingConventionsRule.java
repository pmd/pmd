/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.naming;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTCompilation;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class MethodNamingConventionsRule extends AbstractApexRule {

    public Object visit(ASTCompilation node, Object data) {
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
        ASTClassOrInterfaceBodyDeclaration declaration = node
                .getFirstParentOfType(ASTClassOrInterfaceBodyDeclaration.class);
        List<ASTMarkerAnnotation> annotations = declaration.findDescendantsOfType(ASTMarkerAnnotation.class);
        for (ASTMarkerAnnotation ann : annotations) {
            ASTName name = ann.getFirstChildOfType(ASTName.class);
            if (name != null && name.hasImageEqualTo("Override")) {
                return true;
            }
        }
        return false;
    }
}
