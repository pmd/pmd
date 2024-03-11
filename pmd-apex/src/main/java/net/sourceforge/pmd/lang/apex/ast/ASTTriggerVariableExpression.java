/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.FieldExpression;

public final class ASTTriggerVariableExpression extends AbstractApexNode.Single<FieldExpression> {

    ASTTriggerVariableExpression(FieldExpression fieldExpression) {
        super(fieldExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
