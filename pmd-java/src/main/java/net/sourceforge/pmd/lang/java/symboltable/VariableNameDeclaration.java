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
            return ((Dimensionable) typeNode.getParent()).isArray();
        } else {
            return false;
        }
    }

    public int getArrayDepth() {
        ASTVariableDeclaratorId astVariableDeclaratorId = (ASTVariableDeclaratorId) node;
        ASTType typeNode = astVariableDeclaratorId.getTypeNode();
        if (typeNode != null) {
            return ((Dimensionable) typeNode.getParent()).getArrayDepth();
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
        return getDeclaratorId().isExceptionBlockParameter();
    }

    /**
     * @deprecated use {@link #isTypeInferred()}
     */
    @Deprecated
    public boolean isLambdaTypelessParameter() {
        return isTypeInferred();
    }

    public boolean isTypeInferred() {
        return getDeclaratorId().isTypeInferred();
    }

    public boolean isPrimitiveType() {
        return !isTypeInferred()
                && getAccessNodeParent().getFirstChildOfType(ASTType.class).getChild(0) instanceof ASTPrimitiveType;
    }

    @Override
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
        return !isTypeInferred()
                && getAccessNodeParent().getFirstChildOfType(ASTType.class).getChild(0) instanceof ASTReferenceType;
    }

    public AccessNode getAccessNodeParent() {
        if (node.getParent() instanceof ASTFormalParameter || node.getParent() instanceof ASTLambdaExpression) {
            return (AccessNode) node.getParent();
        }
        return (AccessNode) node.getParent().getParent();
    }

    public ASTVariableDeclaratorId getDeclaratorId() {
        return (ASTVariableDeclaratorId) node;
    }

    private TypeNode getTypeNode() {
        if (isPrimitiveType()) {
            return (TypeNode) getAccessNodeParent().getFirstChildOfType(ASTType.class).getChild(0);
        }
        if (!isTypeInferred()) {
            return (TypeNode) getAccessNodeParent().getFirstChildOfType(ASTType.class).getChild(0).getChild(0);
        }
        return null;
    }

    @Override
    public Class<?> getType() {
        TypeNode typeNode = getTypeNode();
        if (typeNode != null) {
            return typeNode.getType();
        }
        // if there is no type node, then return the type of the declarator id.
        // this might be a inferred type
        return getDeclaratorId().getType();
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
