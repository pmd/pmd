/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.Node;

import apex.jorje.semantic.ast.statement.VariableDeclaration;

public final class ASTVariableDeclaration extends AbstractApexNode<VariableDeclaration> implements Node {

    ASTVariableDeclaration(VariableDeclaration variableDeclaration) {
        super(variableDeclaration);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        if (node.getLocalInfo() != null) {
            return node.getLocalInfo().getName();
        }
        return null;
    }


    public String getType() {
        if (node.getLocalInfo() != null) {
            return node.getLocalInfo().getType().getApexName();
        }
        return null;
    }
}
