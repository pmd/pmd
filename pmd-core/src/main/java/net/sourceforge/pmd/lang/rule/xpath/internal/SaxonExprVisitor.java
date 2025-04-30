/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import net.sf.saxon.expr.AndExpression;
import net.sf.saxon.expr.AxisExpression;
import net.sf.saxon.expr.BinaryExpression;
import net.sf.saxon.expr.BooleanExpression;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.FilterExpression;
import net.sf.saxon.expr.LetExpression;
import net.sf.saxon.expr.OrExpression;
import net.sf.saxon.expr.QuantifiedExpression;
import net.sf.saxon.expr.RootExpression;
import net.sf.saxon.expr.SlashExpression;
import net.sf.saxon.expr.VennExpression;
import net.sf.saxon.expr.sort.DocumentSorter;

abstract class SaxonExprVisitor {
    public Expression visit(DocumentSorter e) {
        Expression base = visit(e.getBaseExpression());
        return new DocumentSorter(base);
    }

    public Expression visit(SlashExpression e) {
        Expression start = visit(e.getStart());
        Expression step = visit(e.getStep());
        return new SlashExpression(start, step);
    }

    public Expression visit(RootExpression e) {
        return e;
    }

    public Expression visit(AxisExpression e) {
        return e;
    }

    public Expression visit(VennExpression e) {
        Expression operand0 = visit(e.getLhsExpression());
        Expression operand1 = visit(e.getRhsExpression());
        return new VennExpression(operand0, e.getOperator(), operand1);
    }

    public Expression visit(FilterExpression e) {
        Expression base = visit(e.getLhsExpression());
        Expression filter = visit(e.getFilter());
        return new FilterExpression(base, filter);
    }

    public Expression visit(BinaryExpression e) {
        Expression base = visit(e.getLhsExpression());
        Expression filter = visit(e.getRhsExpression());

        return new FilterExpression(base, filter);
    }

    public Expression visit(QuantifiedExpression e) {
        return e;
    }

    public Expression visit(LetExpression e) {
        Expression action = visit(e.getAction());
        Expression sequence = visit(e.getSequence());
        LetExpression result = new LetExpression();
        result.setAction(action);
        result.setSequence(sequence);
        result.setVariableQName(e.getVariableQName());
        result.setRequiredType(e.getRequiredType());
        result.setSlotNumber(e.getLocalSlotNumber());
        return result;
    }

    public Expression visit(BooleanExpression e) {
        Expression operand0 = visit(e.getLhsExpression());
        Expression operand1 = visit(e.getRhsExpression());

        return e instanceof AndExpression ? new AndExpression(operand0, operand1)
                                          : new OrExpression(operand0, operand1);
    }

    public Expression visit(Expression expr) {
        Expression result;
        if (expr instanceof DocumentSorter) {
            result = visit((DocumentSorter) expr);
        } else if (expr instanceof SlashExpression) {
            result = visit((SlashExpression) expr);
        } else if (expr instanceof RootExpression) {
            result = visit((RootExpression) expr);
        } else if (expr instanceof AxisExpression) {
            result = visit((AxisExpression) expr);
        } else if (expr instanceof VennExpression) {
            result = visit((VennExpression) expr);
        } else if (expr instanceof FilterExpression) {
            result = visit((FilterExpression) expr);
        } else if (expr instanceof QuantifiedExpression) {
            result = visit((QuantifiedExpression) expr);
        } else if (expr instanceof LetExpression) {
            result = visit((LetExpression) expr);
        } else if (expr instanceof BooleanExpression) {
            result = visit((BooleanExpression) expr);
        } else {
            result = expr;
        }
        return result;
    }
}
