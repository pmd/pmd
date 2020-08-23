/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.Collection;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

public class LooseCouplingRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        if (methodHasOverride(node)) {
            return data;
        }
        Node parent = node.getNthParent(3);
        boolean isType = (TypeHelper.isA(node, Collection.class) || TypeHelper.isA(node, Map.class))
            && !(node.getType() != null && node.getType().isInterface());

        if (isType && (parent instanceof ASTFieldDeclaration || parent instanceof ASTFormalParameter
                || parent instanceof ASTResultType)) {
            addViolation(data, node, node.getImage());
        }
        return data;
    }

    private boolean methodHasOverride(JavaNode node) {
        ASTMethodDeclaration method = node.ancestors(ASTMethodDeclaration.class).first();
        return method != null && method.isAnnotationPresent(Override.class);
    }
}
