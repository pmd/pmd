/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 11:48:50 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.AccessNode;

public class VariableNameDeclaration {

    private ASTVariableDeclaratorId node;

    public VariableNameDeclaration(ASTVariableDeclaratorId node) {
        this.node = node;
    }

    public boolean isExceptionBlockParameter() {
        return node.isExceptionBlockParameter();
    }

    public AccessNode getAccessNodeParent() {
        return (AccessNode)node.jjtGetParent().jjtGetParent();
    }

    public Scope getScope() {
        return node.getScope();
    }

    public int getLine() {
        return node.getBeginLine();
    }

    public String getImage() {
        return node.getImage();
    }

    public boolean equals(Object o) {
        VariableNameDeclaration n = (VariableNameDeclaration)o;
        return n.node.getImage().equals(node.getImage());
    }

    public int hashCode() {
        return node.getImage().hashCode();
    }

    public String toString() {
        return "Variable " + node.getImage() + ":" + node.getBeginLine();
    }
}
