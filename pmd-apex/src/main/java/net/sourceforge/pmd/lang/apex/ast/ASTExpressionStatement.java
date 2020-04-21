/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.util.document.TextRegion;

import apex.jorje.semantic.ast.statement.ExpressionStatement;

public final class ASTExpressionStatement extends AbstractApexNode<ExpressionStatement> {

    ASTExpressionStatement(ExpressionStatement expressionStatement) {
        super(expressionStatement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    protected TextRegion getRegion() {
        if (getNumChildren() > 0) {
            return TextRegion.union(super.getRegion(), ((AbstractApexNode) getChild(0)).getRegion());
        }
        return super.getRegion();
    }
}
