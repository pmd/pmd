/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMarkerAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.BooleanProperty;

public class MethodNamingConventionsRule extends AbstractJavaRule {

    private boolean checkNativeMethods;

    private static final BooleanProperty CHECK_NATIVE_METHODS_DESCRIPTOR = new BooleanProperty("checkNativeMethods",
            "Check native methods", true, 1.0f);

    public MethodNamingConventionsRule() {
        definePropertyDescriptor(CHECK_NATIVE_METHODS_DESCRIPTOR);
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        checkNativeMethods = getProperty(CHECK_NATIVE_METHODS_DESCRIPTOR);
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclarator node, Object data) {
        if (!checkNativeMethods && node.getFirstParentOfType(ASTMethodDeclaration.class).isNative()) {
            return data;
        }

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

    private boolean isOverriddenMethod(ASTMethodDeclarator node) {
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
