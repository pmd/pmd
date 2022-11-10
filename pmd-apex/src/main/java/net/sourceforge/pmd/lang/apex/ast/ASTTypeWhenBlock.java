/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.statement.SwitchStatement;

public final class ASTTypeWhenBlock extends AbstractApexNode.Single<SwitchStatement.WhenType> {

    ASTTypeWhenBlock(SwitchStatement.WhenType whenType) {
        super(whenType);
    }

    public String getType() {
        return node.getType().asCodeString();
    }

    public String getName() {
        return node.getVariableDeclaration().getId().getString();
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
