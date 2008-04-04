package net.sourceforge.pmd.rules;

import java.util.List;

import net.sourceforge.pmd.lang.java.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;

public class MethodWithSameNameAsEnclosingClass extends AbstractJavaRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        List<ASTMethodDeclarator> methods = node.findChildrenOfType(ASTMethodDeclarator.class);
        for (ASTMethodDeclarator m: methods) {
            if (m.hasImageEqualTo(node.getImage())) {
                addViolation(data, m);
            }
        }
        return super.visit(node, data);
    }
}
