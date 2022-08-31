/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.expression.TypeRefExpression;

public class ASTClassRefExpression extends AbstractApexNode.Single<TypeRefExpression> {

    @Deprecated
    @InternalApi
    public ASTClassRefExpression(TypeRefExpression typeRefExpression) {
        super(typeRefExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
