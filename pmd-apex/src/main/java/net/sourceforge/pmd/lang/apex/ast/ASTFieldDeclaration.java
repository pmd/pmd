/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.declaration.FieldDeclaration;

public class ASTFieldDeclaration extends AbstractApexNode.Single<FieldDeclaration> {

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
        return node.getId().getString();
    }
}
