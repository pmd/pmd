/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.FieldExpression;

public class ASTTriggerVariableExpression extends AbstractApexNode.Single<FieldExpression> {

    ASTTriggerVariableExpression(FieldExpression fieldExpression) {
        super(fieldExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
