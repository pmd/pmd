/*
 * User: tom
 * Date: Oct 21, 2002
 * Time: 1:40:54 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.*;

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
        ASTFormalParameters myParams = (ASTFormalParameters)node.jjtGetChild(0);
        ASTFormalParameters otherParams = (ASTFormalParameters)otherMethodDecl.node.jjtGetChild(0);
        for (int i=0;i<node.getParameterCount();i++) {
            ASTFormalParameter myParam = (ASTFormalParameter)myParams.jjtGetChild(i);
            ASTFormalParameter otherParam = (ASTFormalParameter)otherParams.jjtGetChild(i);
            SimpleNode myTypeNode = (SimpleNode)myParam.jjtGetChild(0).jjtGetChild(0);
            SimpleNode otherTypeNode = (SimpleNode)otherParam.jjtGetChild(0).jjtGetChild(0);

            // simple comparison of type images
            // this can be fooled by one method using "String"
            // and the other method using "java.lang.String"
            // once we get real types in here that should get fixed
            if (!myTypeNode.getImage().equals(otherTypeNode.getImage())) {
                return false;
            }

            // if type is ASTPrimitiveType and is an array, make sure the other one is also
        }
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
