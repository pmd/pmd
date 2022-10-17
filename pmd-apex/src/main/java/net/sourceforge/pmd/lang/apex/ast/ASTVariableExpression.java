/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Identifier;

public class ASTVariableExpression extends AbstractApexNode.Single<Identifier> {

    ASTVariableExpression(Identifier identifier) {
        super(identifier);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getString();
    }
}
