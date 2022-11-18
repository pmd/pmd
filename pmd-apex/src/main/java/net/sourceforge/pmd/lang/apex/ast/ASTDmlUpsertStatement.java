/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;

public class ASTDmlUpsertStatement extends AbstractApexNode.Single<Node> {

    ASTDmlUpsertStatement(Node dmlUpsertStatement) {
        super(dmlUpsertStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
