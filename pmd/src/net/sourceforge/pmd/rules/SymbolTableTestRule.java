/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;

public class SymbolTableTestRule extends AbstractRule {

    public Object visit(ASTFieldDeclaration node,Object data) {
        ASTVariableDeclaratorId declaration = node.findChildrenOfType(ASTVariableDeclaratorId.class).get(0);
        for (NameOccurrence no: declaration.getUsages()) {
            SimpleNode location = no.getLocation();
            System.out.println(declaration.getImage() + " is used here: " + location.getImage());
        }
        return data;
    }
}
