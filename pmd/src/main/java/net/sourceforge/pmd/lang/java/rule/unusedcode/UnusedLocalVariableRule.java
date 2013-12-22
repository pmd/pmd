/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.unusedcode;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class UnusedLocalVariableRule extends AbstractJavaRule {

    public Object visit(ASTLocalVariableDeclaration decl, Object data) {
        for (int i = 0; i < decl.jjtGetNumChildren(); i++) {
            if (!(decl.jjtGetChild(i) instanceof ASTVariableDeclarator)) {
                continue;
            }
            ASTVariableDeclaratorId node = (ASTVariableDeclaratorId) decl.jjtGetChild(i).jjtGetChild(0);
            // TODO this isArray() check misses some cases
            // need to add DFAish code to determine if an array
            // is initialized locally or gotten from somewhere else
            if (!node.getNameDeclaration().isArray() && !actuallyUsed(node.getUsages())) {
                addViolation(data, node, node.getNameDeclaration().getImage());
            }
        }
        return data;
    }

    private boolean actuallyUsed(List<NameOccurrence> usages) {
        for (NameOccurrence occ: usages) {
            JavaNameOccurrence jocc = (JavaNameOccurrence)occ;
            if (jocc.isOnLeftHandSide()) {
                continue;
            } else {
                return true;
            }
        }
        return false;
    }

}
