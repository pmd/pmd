/*
 * User: tom
 * Date: Oct 21, 2002
 * Time: 1:40:54 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;

public class MethodNameDeclaration extends AbstractNameDeclaration {

    public MethodNameDeclaration(ASTMethodDeclarator node) {
        super(node);
    }

    public boolean equals(Object o) {
        MethodNameDeclaration n = (MethodNameDeclaration)o;
        return n.getNode().getImage().equals(node.getImage()) &&
            ((ASTMethodDeclarator)n.getNode()).getParameterCount() == ((ASTMethodDeclarator)node).getParameterCount();
    }

    public int hashCode() {
        return node.getImage().hashCode() + ((ASTMethodDeclarator)node).getParameterCount();
    }

    public String toString() {
        return "Method " + node.getImage() + ":" + node.getBeginLine();
    }
}
