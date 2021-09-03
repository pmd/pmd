/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.rule.xpath.internal;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.ArrayList;
import java.util.List;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.VennExpression;
import net.sf.saxon.expr.parser.Token;
import net.sf.saxon.expr.sort.DocumentSorter;

/**
 * Splits a venn expression with the union operator into single expressions.
 * 
 * <p>E.g. "//A | //B | //C" will result in 3 expressions "//A", "//B", and "//C".
 */
class SplitUnions extends SaxonExprVisitor {

    private final List<Expression> expressions = new ArrayList<>();

    @Override
    public Expression visit(VennExpression e) {
        if (e.getOperator() == Token.UNION) {
            for (Expression operand : listOf(e.getLhsExpression(), e.getRhsExpression())) {
                if (operand instanceof VennExpression) {
                    visit(operand);
                } else {
                    expressions.add(operand);
                }
            }
        }
        return e;
    }

    @Override
    public Expression visit(Expression e) {
        // only flatten toplevel unions
        if (e instanceof VennExpression || e instanceof DocumentSorter) {
            return super.visit(e);
        } else {
            return e;
        }
    }

    public List<Expression> getExpressions() {
        return expressions;
    }
}
