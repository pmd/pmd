/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.symboltable.AbstractNameDeclaration;

public class MethodNameDeclaration extends AbstractNameDeclaration {

    public MethodNameDeclaration(ASTMethodDeclarator node) {
        super(node);
    }

    public int getParameterCount() {
        return ((ASTMethodDeclarator) node).getParameterCount();
    }

    public boolean isVarargs() {
        ASTFormalParameters params = (ASTFormalParameters) node.getChild(0);
        if (params.getParameterCount() == 0) {
            return false;
        }

        // If it's a varargs, it HAS to be the last parameter
        ASTFormalParameter p = (ASTFormalParameter) params.getChild(params.getParameterCount() - 1);
        return p.isVarargs();
    }

    public ASTMethodDeclarator getMethodNameDeclaratorNode() {
        return (ASTMethodDeclarator) node;
    }

    public String getParameterDisplaySignature() {
        StringBuilder sb = new StringBuilder("(");
        ASTFormalParameters params = (ASTFormalParameters) node.getChild(0);
        // TODO - this can be optimized - add [0] then ,[n] in a loop.
        // no need to trim at the end
        for (int i = 0; i < ((ASTMethodDeclarator) node).getParameterCount(); i++) {
            ASTFormalParameter p = (ASTFormalParameter) params.getChild(i);
            sb.append(p.getTypeNode().getTypeImage());
            if (p.isVarargs()) {
                sb.append("...");
            }
            sb.append(',');
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MethodNameDeclaration)) {
            return false;
        }

        MethodNameDeclaration other = (MethodNameDeclaration) o;

        // compare name
        if (!other.node.getImage().equals(node.getImage())) {
            return false;
        }

        // compare parameter count - this catches the case where there are no
        // params, too
        if (((ASTMethodDeclarator) other.node).getParameterCount() != ((ASTMethodDeclarator) node)
                .getParameterCount()) {
            return false;
        }

        // compare parameter types
        ASTFormalParameters myParams = (ASTFormalParameters) node.getChild(0);
        ASTFormalParameters otherParams = (ASTFormalParameters) other.node.getChild(0);
        for (int i = 0; i < ((ASTMethodDeclarator) node).getParameterCount(); i++) {
            ASTFormalParameter myParam = (ASTFormalParameter) myParams.getChild(i);
            ASTFormalParameter otherParam = (ASTFormalParameter) otherParams.getChild(i);

            // Compare vararg
            if (myParam.isVarargs() != otherParam.isVarargs()) {
                return false;
            }

            Node myTypeNode = myParam.getTypeNode().getChild(0);
            Node otherTypeNode = otherParam.getTypeNode().getChild(0);

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
                myTypeImg = myTypeNode.getChild(0).getImage();
                otherTypeImg = otherTypeNode.getChild(0).getImage();
            }

            if (!myTypeImg.equals(otherTypeImg)) {
                return false;
            }

            // if type is ASTPrimitiveType and is an array, make sure the other
            // one is also
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = node.getImage().hashCode() * 31 + ((ASTMethodDeclarator) node).getParameterCount();

        ASTFormalParameters myParams = (ASTFormalParameters) node.getChild(0);
        for (int i = 0; i < ((ASTMethodDeclarator) node).getParameterCount(); i++) {
            ASTFormalParameter myParam = (ASTFormalParameter) myParams.getChild(i);
            Node myTypeNode = myParam.getTypeNode().getChild(0);

            String myTypeImg;
            if (myTypeNode instanceof ASTPrimitiveType) {
                myTypeImg = myTypeNode.getImage();
            } else {
                myTypeImg = myTypeNode.getChild(0).getImage();
            }

            hash = hash * 31 + myTypeImg.hashCode();
        }

        return hash;
    }

    @Override
    public String toString() {
        return "Method " + node.getImage() + ", line " + node.getBeginLine() + ", params = "
                + ((ASTMethodDeclarator) node).getParameterCount();
    }
}
