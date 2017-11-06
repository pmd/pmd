/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class MethodWithSameNameAsEnclosingClassRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        List<ASTMethodDeclarator> methods = node.findDescendantsOfType(ASTMethodDeclarator.class);
        for (ASTMethodDeclarator m : methods) {
            if (m.hasImageEqualTo(node.getImage())) {
                addViolation(data, m);
            }
        }
        return super.visit(node, data);
    }
}
