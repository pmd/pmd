/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.util.Iterator;

public class SymbolTableTestRule extends AbstractRule implements Rule {

    public Object visit(ASTFieldDeclaration node,Object data) {
        ASTVariableDeclaratorId declaration = (ASTVariableDeclaratorId)node.findChildrenOfType(ASTVariableDeclaratorId.class).get(0);
        for (Iterator iter = declaration.getUsages().iterator();iter.hasNext();) {
            NameOccurrence no = (NameOccurrence)iter.next();
            SimpleNode location = no.getLocation();
            System.out.println(declaration.getImage() + " is used here: " + location.getImage());
        }
        return data;
    }
}
