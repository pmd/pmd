/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.FieldDeclarationStatements;

public class ASTFieldDeclarationStatements extends AbstractApexNode<FieldDeclarationStatements> {

    public ASTFieldDeclarationStatements(FieldDeclarationStatements fieldDeclarationStatements) {
        super(fieldDeclarationStatements);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
