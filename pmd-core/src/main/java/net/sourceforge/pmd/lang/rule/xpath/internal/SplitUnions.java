/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.ArrayList;
import java.util.List;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.Token;
import net.sf.saxon.expr.VennExpression;

/**
 * Splits a venn expression with the union operator into single expressions.
 * 
 * <p>E.g. "//A | //B | //C" will result in 3 expressions "//A", "//B", and "//C".
 */
class SplitUnions extends SaxonExprVisitor {
    private List<Expression> expressions = new ArrayList<>();

    @Override
    public Expression visit(VennExpression e) {
        if (e.getOperator() == Token.UNION) {
            for (Expression operand : e.getOperands()) {
                if (operand instanceof VennExpression) {
                    visit(operand);
                } else {
                    expressions.add(operand);
                }
            }
        }
        return e;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }
}
