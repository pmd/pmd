/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.Dimensionable;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.symboltable.AbstractNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class VariableNameDeclaration extends AbstractNameDeclaration implements TypedNameDeclaration {

    public VariableNameDeclaration(ASTVariableDeclaratorId node) {
        super(node);
    }

    @Override
    public Scope getScope() {
        return node.getScope().getEnclosingScope(ClassScope.class);
    }

    public boolean isArray() {
        ASTVariableDeclaratorId astVariableDeclaratorId = (ASTVariableDeclaratorId) node;
        ASTType typeNode = astVariableDeclaratorId.getTypeNode();
        if (typeNode != null) {
            return ((Dimensionable) typeNode.jjtGetParent()).isArray();
        } else {
            return false;
        }
    }
    
    public int getArrayDepth() {
        ASTVariableDeclaratorId astVariableDeclaratorId = (ASTVariableDeclaratorId) node;
        ASTType typeNode = astVariableDeclaratorId.getTypeNode();
        if (typeNode != null) {
            return ((Dimensionable) typeNode.jjtGetParent()).getArrayDepth();
        } else {
            return 0;
        }
    }

    public boolean isVarargs() {
        ASTVariableDeclaratorId astVariableDeclaratorId = (ASTVariableDeclaratorId) node;
        ASTFormalParameter parameter = astVariableDeclaratorId.getFirstParentOfType(ASTFormalParameter.class);
        return parameter != null && parameter.isVarargs();
    }

    public boolean isExceptionBlockParameter() {
        return ((ASTVariableDeclaratorId) node).isExceptionBlockParameter();
    }

    public boolean isLambdaTypelessParameter() {
        return getAccessNodeParent() instanceof ASTLambdaExpression;
    }

    public boolean isPrimitiveType() {
        return !isLambdaTypelessParameter()
                && getAccessNodeParent().getFirstChildOfType(ASTType.class).jjtGetChild(0) instanceof ASTPrimitiveType;
    }

    public String getTypeImage() {
        TypeNode typeNode = getTypeNode();
        if (typeNode != null) {
            return typeNode.getImage();
        }
        return null;
    }

    /**
     * Note that an array of primitive types (int[]) is a reference type.
     */
    public boolean isReferenceType() {
        return !isLambdaTypelessParameter()
                && getAccessNodeParent().getFirstChildOfType(ASTType.class).jjtGetChild(0) instanceof ASTReferenceType;
    }

    public AccessNode getAccessNodeParent() {
        if (node.jjtGetParent() instanceof ASTFormalParameter || node.jjtGetParent() instanceof ASTLambdaExpression) {
            return (AccessNode) node.jjtGetParent();
        }
        return (AccessNode) node.jjtGetParent().jjtGetParent();
    }

    public ASTVariableDeclaratorId getDeclaratorId() {
        return (ASTVariableDeclaratorId) node;
    }

    private TypeNode getTypeNode() {
        if (isPrimitiveType()) {
            return (TypeNode) getAccessNodeParent().getFirstChildOfType(ASTType.class).jjtGetChild(0);
        }
        if (!isLambdaTypelessParameter()) {
            return (TypeNode) getAccessNodeParent().getFirstChildOfType(ASTType.class).jjtGetChild(0).jjtGetChild(0);
        }
        return null;
    }

    public Class<?> getType() {
        TypeNode typeNode = getTypeNode();
        if (typeNode != null) {
            return typeNode.getType();
        }
        return null;
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
