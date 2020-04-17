/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.statement.FieldDeclaration;

public class ASTFieldDeclaration extends AbstractApexNode<FieldDeclaration> {

    @Deprecated
    @InternalApi
    public ASTFieldDeclaration(FieldDeclaration fieldDeclaration) {
        super(fieldDeclaration);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return getName();
    }

    public String getName() {
        if (node.getFieldInfo() != null) {
            return node.getFieldInfo().getName();
        }
        ASTVariableExpression variable = getFirstChildOfType(ASTVariableExpression.class);
        if (variable != null) {
            return variable.getImage();
        }
        return null;
    }
}
