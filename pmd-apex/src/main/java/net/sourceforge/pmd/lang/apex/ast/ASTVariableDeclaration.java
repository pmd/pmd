/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.VariableDeclaration;

public class ASTVariableDeclaration extends AbstractApexNode<VariableDeclaration> {

    public ASTVariableDeclaration(VariableDeclaration variableDeclaration) {
        super(variableDeclaration);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getLocalInfo().getName();
    }
}
