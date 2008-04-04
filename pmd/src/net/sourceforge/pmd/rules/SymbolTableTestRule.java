/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.symboltable.NameOccurrence;

public class SymbolTableTestRule extends AbstractJavaRule {

    public Object visit(ASTFieldDeclaration node,Object data) {
        ASTVariableDeclaratorId declaration = node.findChildrenOfType(ASTVariableDeclaratorId.class).get(0);
        for (NameOccurrence no: declaration.getUsages()) {
            Node location = no.getLocation();
            System.out.println(declaration.getImage() + " is used here: " + location.getImage());
        }
        return data;
    }
}
