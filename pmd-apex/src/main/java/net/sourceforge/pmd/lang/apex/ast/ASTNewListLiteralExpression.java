/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.initializer.ValuesInitializer;

public final class ASTNewListLiteralExpression extends AbstractApexNode.Single<ValuesInitializer> {

    ASTNewListLiteralExpression(ValuesInitializer valuesInitializer) {
        super(valuesInitializer);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
