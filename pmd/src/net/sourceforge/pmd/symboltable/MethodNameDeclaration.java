/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTPrimitiveType;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.SimpleNode;

public class MethodNameDeclaration extends AbstractNameDeclaration {

    public MethodNameDeclaration(ASTMethodDeclarator node) {
        super(node);
    }

    public String getParameterDisplaySignature() {
        StringBuffer sb = new StringBuffer("(");

        ASTFormalParameters params = (ASTFormalParameters) node.jjtGetChild(0);
        for (int i = 0; i < ((ASTMethodDeclarator) node).getParameterCount(); i++) {
            ASTFormalParameter p = (ASTFormalParameter) params.jjtGetChild(i);
            sb.append(((ASTType)p.getFirstChildOfType(ASTType.class)).getTypeImage());
            sb.append(",");
        }

        if (sb.charAt(sb.length()-1) == ',') {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString() + ")";
    }

    public boolean equals(Object o) {
        MethodNameDeclaration other = (MethodNameDeclaration) o;

        // compare name
        if (!other.node.getImage().equals(node.getImage())) {
            return false;
        }

        // compare parameter count - this catches the case where there are no params, too
        if (((ASTMethodDeclarator) (other.node)).getParameterCount() != ((ASTMethodDeclarator) node).getParameterCount()) {
            return false;
        }

        // compare parameter types
        ASTFormalParameters myParams = (ASTFormalParameters) node.jjtGetChild(0);
        ASTFormalParameters otherParams = (ASTFormalParameters) other.node.jjtGetChild(0);
        for (int i = 0; i < ((ASTMethodDeclarator) node).getParameterCount(); i++) {
            ASTFormalParameter myParam = (ASTFormalParameter) myParams.jjtGetChild(i);
            ASTFormalParameter otherParam = (ASTFormalParameter) otherParams.jjtGetChild(i);

            SimpleNode myTypeNode = (SimpleNode) myParam.jjtGetChild(0).jjtGetChild(0);
            SimpleNode otherTypeNode = (SimpleNode) otherParam.jjtGetChild(0).jjtGetChild(0);

            // compare primitive vs reference type
            if (myTypeNode.getClass() != otherTypeNode.getClass()) {
                return false;
            }

            // simple comparison of type images
            // this can be fooled by one method using "String"
            // and the other method using "java.lang.String"
            // once we get real types in here that should get fixed
            String myTypeImg;
            String otherTypeImg;
            if (myTypeNode instanceof ASTPrimitiveType) {
                myTypeImg = myTypeNode.getImage();
                otherTypeImg = otherTypeNode.getImage();
            } else {
                myTypeImg = ((SimpleNode)(myTypeNode.jjtGetChild(0))).getImage();
                otherTypeImg = ((SimpleNode)(otherTypeNode.jjtGetChild(0))).getImage();
            }

            if (!myTypeImg.equals(otherTypeImg)) {
                return false;
            }

            // if type is ASTPrimitiveType and is an array, make sure the other one is also
        }
        return true;
    }

    public int hashCode() {
        return node.getImage().hashCode() + ((ASTMethodDeclarator) node).getParameterCount();
    }

    public String toString() {
        return "Method " + node.getImage();
    }
}
