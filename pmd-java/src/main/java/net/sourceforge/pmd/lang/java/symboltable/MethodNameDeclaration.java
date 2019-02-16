/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import java.util.Objects;

import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.symboltable.AbstractNameDeclaration;

public class MethodNameDeclaration extends AbstractNameDeclaration {

    public MethodNameDeclaration(ASTMethodDeclarator node) {
        super(node);
    }

    public int getParameterCount() {
        return ((ASTMethodDeclarator) node).getParameterCount();
    }

    public boolean isVarargs() {
        ASTFormalParameters params = (ASTFormalParameters) node.jjtGetChild(0);
        if (params.getParameterCount() == 0) {
            return false;
        }

        // If it's a varargs, it HAS to be the last parameter
        ASTFormalParameter p = (ASTFormalParameter) params.jjtGetChild(params.getParameterCount() - 1);
        return p.isVarargs();
    }

    public ASTMethodDeclarator getMethodNameDeclaratorNode() {
        return (ASTMethodDeclarator) node;
    }

    public String getParameterDisplaySignature() {
        StringBuilder sb = new StringBuilder("(");
        ASTFormalParameters params = (ASTFormalParameters) node.jjtGetChild(0);
        // TODO - this can be optimized - add [0] then ,[n] in a loop.
        // no need to trim at the end
        for (int i = 0; i < ((ASTMethodDeclarator) node).getParameterCount(); i++) {
            ASTFormalParameter p = (ASTFormalParameter) params.jjtGetChild(i);
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

    public ASTMethodDeclarator getDeclarator() {
        return (ASTMethodDeclarator) node;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MethodNameDeclaration)) {
            return false;
        }

        MethodNameDeclaration other = (MethodNameDeclaration) o;

        // compare name
        if (!other.getName().equals(getName())) {
            return false;
        }

        // compare parameter count - this catches the case where there are no
        // params, too
        if (other.getParameterCount() != this.getParameterCount()) {
            return false;
        }

        // compare parameter types

        ASTFormalParameters myParams = getDeclarator().getFormalParameters();
        ASTFormalParameters otherParams = other.getDeclarator().getFormalParameters();

        for (int i = 0; i < getParameterCount(); i++) {
            ASTFormalParameter myParam = myParams.jjtGetChild(i);
            ASTFormalParameter otherParam = otherParams.jjtGetChild(i);

            // Compare vararg
            if (myParam.isVarargs() != otherParam.isVarargs()) {
                return false;
            }

            ASTType myTypeNode = myParam.getTypeNode();
            ASTType otherTypeNode = otherParam.getTypeNode();

            // compare primitive vs reference type
            if (myTypeNode.getClass() != otherTypeNode.getClass()) {
                return false;
            }

            // if we could resolve the first one, assume we could resolve the second
            if (myTypeNode.getTypeDefinition() != null
                && !myTypeNode.getTypeDefinition().equals(otherTypeNode.getTypeDefinition())) {
                return false;
            }

            // fallback on simple comparison of type images
            // this can be fooled by one method using "String"
            // and the other method using "java.lang.String"
            // once we get real types in here that should get fixed
            if (!Objects.equals(myTypeNode.getTypeImage(), otherTypeNode.getTypeImage())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = node.getImage().hashCode() * 31 + ((ASTMethodDeclarator) node).getParameterCount();

        ASTFormalParameters myParams = (ASTFormalParameters) node.jjtGetChild(0);
        for (ASTFormalParameter myParam : myParams) {
            if (!myParam.isTypeInferred()) {
                String myTypeImg = myParam.getTypeNode().getTypeImage();
                hash = hash * 31 + myTypeImg.hashCode();
            }
        }

        return hash;
    }

    @Override
    public String toString() {
        return "Method " + node.getImage() + ", line " + node.getBeginLine() + ", params = "
                + ((ASTMethodDeclarator) node).getParameterCount();
    }
}
