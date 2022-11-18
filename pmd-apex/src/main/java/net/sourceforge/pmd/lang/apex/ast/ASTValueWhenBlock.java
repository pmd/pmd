/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.statement.SwitchStatement;

public final class ASTValueWhenBlock extends AbstractApexNode.Single<SwitchStatement.WhenValue> {

    ASTValueWhenBlock(SwitchStatement.WhenValue whenValue) {
        super(whenValue);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
