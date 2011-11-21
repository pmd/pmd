/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.Dimensionable;
import net.sourceforge.pmd.lang.java.ast.TypeNode;

public class VariableNameDeclaration extends AbstractNameDeclaration {

    public VariableNameDeclaration(ASTVariableDeclaratorId node) {
	super(node);
    }

    @Override
    public Scope getScope() {
	return node.getScope().getEnclosingClassScope();
    }

    public boolean isArray() {
	ASTVariableDeclaratorId astVariableDeclaratorId = (ASTVariableDeclaratorId) node;
	ASTType typeNode = astVariableDeclaratorId.getTypeNode();
	return ((Dimensionable) typeNode.jjtGetParent()).isArray();
    }

    public boolean isExceptionBlockParameter() {
	return ((ASTVariableDeclaratorId) node).isExceptionBlockParameter();
    }

    public boolean isPrimitiveType() {
	return ((Node) getAccessNodeParent()).jjtGetChild(0).jjtGetChild(0) instanceof ASTPrimitiveType;
    }

    public String getTypeImage() {
	if (isPrimitiveType()) {
	    return ((Node) getAccessNodeParent()).jjtGetChild(0).jjtGetChild(0).getImage();
	}
	return ((Node) getAccessNodeParent()).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).getImage();
    }

    /**
     * Note that an array of primitive types (int[]) is a reference type.
     */
    public boolean isReferenceType() {
	return ((Node) getAccessNodeParent()).jjtGetChild(0).jjtGetChild(0) instanceof ASTReferenceType;
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

    public Class<?> getType() {
	return ((TypeNode) node).getType();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VariableNameDeclaration)) {
            return false;
        }
	VariableNameDeclaration n = (VariableNameDeclaration) o;
	return n.node.getImage().equals(node.getImage());
    }

    @Override
    public int hashCode() {
	return node.getImage().hashCode();
    }

    @Override
    public String toString() {
	return "Variable: image = '" + node.getImage() + "', line = " + node.getBeginLine();
    }
}
