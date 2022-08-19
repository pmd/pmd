/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.initializer.Initializer;

public class ASTNewMapLiteralExpression extends AbstractApexNode.Single<Initializer> {

    @Deprecated
    @InternalApi
    public ASTNewMapLiteralExpression(Initializer initializer) {
        super(initializer);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
