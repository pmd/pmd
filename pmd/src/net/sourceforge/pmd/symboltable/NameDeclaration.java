/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 11:48:50 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;

public class NameDeclaration {

    private SimpleNode node;

    public NameDeclaration(SimpleNode node) {
        this.node = node;
    }

    public SimpleNode getNode() {
        return node;
    }

    public boolean isExceptionBlockParameter() {
        if (node instanceof ASTVariableDeclaratorId) {
            return ((ASTVariableDeclaratorId)node).isExceptionBlockParameter();
        }
        return false;
    }

    public int getLine() {
        return node.getBeginLine();
    }

    public String getImage() {
        return node.getImage();
    }

    public boolean equals(Object o) {
        NameDeclaration n = (NameDeclaration)o;
        return n.getImage().equals(node.getImage());
    }

    public int hashCode() {
        return node.getImage().hashCode();
    }

    public String toString() {
        return node.getImage() + ":" + node.getBeginLine();
    }
}
