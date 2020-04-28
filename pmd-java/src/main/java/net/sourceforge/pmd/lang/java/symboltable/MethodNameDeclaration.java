/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import java.util.List;
import java.util.Objects;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.symboltable.AbstractNameDeclaration;

public class MethodNameDeclaration extends AbstractNameDeclaration {

    public MethodNameDeclaration(ASTMethodDeclaration node) {
        super(node);
    }

    public int getParameterCount() {
        return getDeclarator().getArity();
    }

    public boolean isVarargs() {
        return getDeclarator().isVarargs();
    }

    public String getParameterDisplaySignature() {
        StringBuilder sb = new StringBuilder("(");
        // TODO - this can be written with Streams and Collectors::joining
        for (ASTFormalParameter p : getDeclarator().getFormalParameters()) {
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

    public ASTMethodDeclaration getDeclarator() {
        return (ASTMethodDeclaration) node;
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

        List<ASTFormalParameter> myParams = IteratorUtil.toList(getDeclarator().getFormalParameters().iterator());
        List<ASTFormalParameter> otherParams = IteratorUtil.toList(other.getDeclarator().getFormalParameters().iterator());

        for (int i = 0; i < myParams.size(); i++) {
            ASTFormalParameter myParam = myParams.get(i);
            ASTFormalParameter otherParam = otherParams.get(i);

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
        ASTMethodDeclaration declaration = getDeclarator();
        int hash = declaration.getName().hashCode() * 31 + declaration.getArity();

        for (ASTFormalParameter myParam : declaration.getFormalParameters()) {
            String myTypeImg = myParam.getTypeNode().getTypeImage();
            hash = hash * 31 + myTypeImg.hashCode();
        }

        return hash;
    }

    @Override
    public String toString() {
        return node.toString();
    }
}
