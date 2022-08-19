/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.initializer.ValuesInitializer;

public class ASTNewSetLiteralExpression extends AbstractApexNode.Single<ValuesInitializer> {

    @Deprecated
    @InternalApi
    public ASTNewSetLiteralExpression(ValuesInitializer valuesInitializer) {
        super(valuesInitializer);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
