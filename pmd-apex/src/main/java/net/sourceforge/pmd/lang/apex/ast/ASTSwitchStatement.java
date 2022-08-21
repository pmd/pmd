/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.statement.SwitchStatement;

public final class ASTSwitchStatement extends AbstractApexNode.Single<SwitchStatement> {

    ASTSwitchStatement(SwitchStatement switchStatement) {
        super(switchStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
