/*
 * User: tom
 * Date: Oct 21, 2002
 * Time: 1:40:54 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;

public class MethodNameDeclaration {

    private ASTMethodDeclarator node;

    public MethodNameDeclaration(ASTMethodDeclarator node) {
        this.node = node;
    }

    public boolean equals(Object o) {
        MethodNameDeclaration n = (MethodNameDeclaration)o;
        return n.node.getImage().equals(node.getImage()) && n.node.getParameterCount() == node.getParameterCount();
    }

    public int getLine() {
        return node.getBeginLine();
    }

    public String getImage() {
        return node.getImage();
    }

    public int hashCode() {
        return node.getImage().hashCode() + node.getParameterCount();
    }

    public String toString() {
        return "Method " + node.getImage() + ":" + node.getBeginLine();
    }
}
