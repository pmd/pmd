/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 11:48:50 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;

public class VariableNameDeclaration extends AbstractNameDeclaration {

    public VariableNameDeclaration(SimpleNode node) {
        super(node);
    }

    public boolean isExceptionBlockParameter() {
        if (node instanceof ASTVariableDeclaratorId) {
            return ((ASTVariableDeclaratorId)node).isExceptionBlockParameter();
        }
        return false;
    }

    public boolean equals(Object o) {
        VariableNameDeclaration n = (VariableNameDeclaration)o;
        return n.getImage().equals(node.getImage());
    }

    public int hashCode() {
        return node.getImage().hashCode();
    }

    public String toString() {
        return "Variable " + node.getImage() + ":" + node.getBeginLine();
    }
}
