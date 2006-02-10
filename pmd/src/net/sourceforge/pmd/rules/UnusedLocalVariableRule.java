/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.util.Iterator;
import java.util.List;

public class UnusedLocalVariableRule extends AbstractRule {

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

    private boolean actuallyUsed(List usages) {
        for (Iterator j = usages.iterator(); j.hasNext();) {
            NameOccurrence occ = (NameOccurrence) j.next();
            if (occ.isOnLeftHandSide()) {
                continue;
            } else {
                return true;
            }
        }
        return false;
    }

}
