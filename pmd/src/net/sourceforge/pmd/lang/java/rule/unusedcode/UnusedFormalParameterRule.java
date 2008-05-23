/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.unusedcode;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;

public class UnusedFormalParameterRule extends AbstractJavaRule {

    public Object visit(ASTConstructorDeclaration node, Object data) {
        check(node, data);
        return data;
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        if (!node.isPrivate() && !hasProperty("checkall")) {
            return data;
        }
        if (!node.isNative()) {
            check(node, data);
        }
        return data;
    }

    private void check(Node node, Object data) {
        Node parent = node.jjtGetParent().jjtGetParent().jjtGetParent();
        if (parent instanceof ASTClassOrInterfaceDeclaration && !((ASTClassOrInterfaceDeclaration) parent).isInterface()) {
            Map<VariableNameDeclaration, List<NameOccurrence>> vars = ((JavaNode)node).getScope().getVariableDeclarations();
            for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry: vars.entrySet()) {
                VariableNameDeclaration nameDecl = entry.getKey();
                if (actuallyUsed(nameDecl, entry.getValue())) {
                    continue;
                }
                addViolation(data, node, new Object[]{node instanceof ASTMethodDeclaration ? "method" : "constructor", nameDecl.getImage()});
            }
        }
    }

    private boolean actuallyUsed(VariableNameDeclaration nameDecl, List<NameOccurrence> usages) {
        for (NameOccurrence occ: usages) {
            if (occ.isOnLeftHandSide()) {
                if (nameDecl.isArray() && occ.getLocation().jjtGetParent().jjtGetParent().jjtGetNumChildren() > 1) {
                    // array element access
                    return true;
                }
                continue;
            } else {
                return true;
            }
        }
        return false;
    }


}
