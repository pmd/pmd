/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTPrimitiveType;
import net.sourceforge.pmd.ast.ASTReferenceType;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.AccessNode;
import net.sourceforge.pmd.ast.Dimensionable;
import net.sourceforge.pmd.ast.SimpleNode;

public class VariableNameDeclaration extends AbstractNameDeclaration {

    public VariableNameDeclaration(ASTVariableDeclaratorId node) {
        super(node);
    }

    public Scope getScope() {
        return node.getScope().getEnclosingClassScope();
    }

    public boolean isArray() {
        ASTVariableDeclaratorId astVariableDeclaratorId = (ASTVariableDeclaratorId) node;
        ASTType typeNode = astVariableDeclaratorId.getTypeNode();
        return ((Dimensionable) (typeNode.jjtGetParent())).isArray();
    }

    public boolean isExceptionBlockParameter() {
        return ((ASTVariableDeclaratorId) node).isExceptionBlockParameter();
    }

    public boolean isPrimitiveType() {
        return getAccessNodeParent().jjtGetChild(0).jjtGetChild(0) instanceof ASTPrimitiveType;
    }

    public String getTypeImage() {
        if (isPrimitiveType()) {
            return ((SimpleNode) (getAccessNodeParent().jjtGetChild(0).jjtGetChild(0))).getImage();
        }
        return ((SimpleNode) getAccessNodeParent().jjtGetChild(0).jjtGetChild(0).jjtGetChild(0)).getImage();
    }

    /**
     * Note that an array of primitive types (int[]) is a reference type.
     */
    public boolean isReferenceType() {
        return getAccessNodeParent().jjtGetChild(0).jjtGetChild(0) instanceof ASTReferenceType;
    }

    public AccessNode getAccessNodeParent() {
        if (node.jjtGetParent() instanceof ASTFormalParameter) {
            return (AccessNode) node.jjtGetParent();
        }
        return (AccessNode) node.jjtGetParent().jjtGetParent();
    }

    public ASTVariableDeclaratorId getDeclaratorId() {
        return (ASTVariableDeclaratorId) node;
    }

    public boolean equals(Object o) {
        VariableNameDeclaration n = (VariableNameDeclaration) o;
        return n.node.getImage().equals(node.getImage());
    }

    public int hashCode() {
        return node.getImage().hashCode();
    }

    public String toString() {
        return "Variable: image = '" + node.getImage() + "', line = " + node.getBeginLine();
    }
}
