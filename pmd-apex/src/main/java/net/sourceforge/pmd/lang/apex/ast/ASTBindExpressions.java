/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.BindExpressions;

public final class ASTBindExpressions extends AbstractApexNode<BindExpressions> {

    ASTBindExpressions(BindExpressions bindExpressions) {
        super(bindExpressions);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
