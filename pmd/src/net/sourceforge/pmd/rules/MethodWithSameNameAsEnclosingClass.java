package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;

import java.util.List;

public class MethodWithSameNameAsEnclosingClass extends AbstractRule {

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
