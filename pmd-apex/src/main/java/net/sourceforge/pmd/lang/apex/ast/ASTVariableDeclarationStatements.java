/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.VariableDeclarationStatements;

public class ASTVariableDeclarationStatements extends AbstractApexNode<VariableDeclarationStatements> {

    public ASTVariableDeclarationStatements(VariableDeclarationStatements variableDeclarationStatements) {
        super(variableDeclarationStatements);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
