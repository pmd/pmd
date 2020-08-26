/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public final class ASTVariableOrConstantDeclaratorId extends AbstractPLSQLNode {
    private int arrayDepth;
    private NameDeclaration nameDeclaration;

    ASTVariableOrConstantDeclaratorId(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptPlsqlVisitor(PlsqlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public NameDeclaration getNameDeclaration() {
        return nameDeclaration;
    }

    void setNameDeclaration(NameDeclaration decl) {
        nameDeclaration = decl;
    }

    public List<NameOccurrence> getUsages() {
        return getScope().getDeclarations().get(nameDeclaration);
    }

    void bumpArrayDepth() {
        arrayDepth++;
    }

    public int getArrayDepth() {
        return arrayDepth;
    }

    public boolean isArray() {
        return arrayDepth > 0;
    }

    public Node getTypeNameNode() {
        if (getParent() instanceof ASTFormalParameter) {
            return findTypeNameNode(getParent());
        } else if (getParent().getParent() instanceof ASTVariableOrConstantDeclaration
                || getParent().getParent() instanceof ASTFieldDeclaration) {
            return findTypeNameNode(getParent().getParent());
        }
        throw new RuntimeException(
                "Don't know how to get the type for anything other than ASTLocalVariableDeclaration/ASTFormalParameter/ASTFieldDeclaration");
    }

    public ASTDatatype getTypeNode() {
        if (getParent() instanceof ASTFormalParameter) {
            return ((ASTFormalParameter) getParent()).getTypeNode();
        } else {
            Node n = getParent().getParent();
            if (n instanceof ASTVariableOrConstantDeclaration || n instanceof ASTFieldDeclaration) {
                return n.getFirstChildOfType(ASTDatatype.class);
            }
        }
        throw new RuntimeException(
                "Don't know how to get the type for anything other than ASTLocalVariableDeclaration/ASTFormalParameter/ASTFieldDeclaration");
    }

    private Node findTypeNameNode(Node node) {
        ASTDatatype typeNode = (ASTDatatype) node.getChild(0);
        return typeNode.getChild(0);
    }

}
