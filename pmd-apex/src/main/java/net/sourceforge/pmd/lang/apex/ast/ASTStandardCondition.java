/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.condition.StandardCondition;

public final class ASTStandardCondition extends AbstractApexNode<StandardCondition> {

    ASTStandardCondition(StandardCondition standardCondition) {
        super(standardCondition);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
