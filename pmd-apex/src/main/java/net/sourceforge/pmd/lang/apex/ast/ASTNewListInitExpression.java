/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.initializer.Initializer;

public class ASTNewListInitExpression extends AbstractApexNode.Single<Initializer> {

    ASTNewListInitExpression(Initializer initializer) {
        super(initializer);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
