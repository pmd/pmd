/*
 * User: tom
 * Date: Oct 21, 2002
 * Time: 1:40:54 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.ASTFormalParameters;

public class MethodNameDeclaration {

    private ASTMethodDeclarator node;

    public MethodNameDeclaration(ASTMethodDeclarator node) {
        this.node = node;
    }

    public boolean equals(Object o) {
        MethodNameDeclaration otherMethodDecl = (MethodNameDeclaration)o;

        // compare method name
        if (!otherMethodDecl.node.getImage().equals(node.getImage())) {
            return false;
        }

        // compare parameter count - this catches the case where there are no params, too
        if (otherMethodDecl.node.getParameterCount() != node.getParameterCount()) {
            return false;
        }

        // compare parameter types
/*
        ASTFormalParameters myParams = (ASTFormalParameters)node.jjtGetChild(0);
        ASTFormalParameters otherParams = (ASTFormalParameters)otherMethodDecl.node.jjtGetChild(0);
        for (int i=0;i<node.getParameterCount();i++) {


        }
*/
        return true;
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
