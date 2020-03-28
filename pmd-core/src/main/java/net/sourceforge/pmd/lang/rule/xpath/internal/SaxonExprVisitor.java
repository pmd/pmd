/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import net.sf.saxon.expr.AxisExpression;
import net.sf.saxon.expr.BooleanExpression;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.FilterExpression;
import net.sf.saxon.expr.LazyExpression;
import net.sf.saxon.expr.LetExpression;
import net.sf.saxon.expr.PathExpression;
import net.sf.saxon.expr.QuantifiedExpression;
import net.sf.saxon.expr.RootExpression;
import net.sf.saxon.expr.VennExpression;
import net.sf.saxon.sort.DocumentSorter;

abstract class SaxonExprVisitor {
    public Expression visit(DocumentSorter e) {
        Expression base = visit(e.getBaseExpression());
        return new DocumentSorter(base);
    }

    public Expression visit(PathExpression e) {
        Expression start = visit(e.getStartExpression());
        Expression step = visit(e.getStepExpression());
        return new PathExpression(start, step);
    }

    public Expression visit(RootExpression e) {
        return e;
    }

    public Expression visit(AxisExpression e) {
        return e;
    }

    public Expression visit(VennExpression e) {
        final Expression[] operands = e.getOperands();
        Expression operand0 = visit(operands[0]);
        Expression operand1 = visit(operands[1]);
        return new VennExpression(operand0, e.getOperator(), operand1);
    }

    public Expression visit(FilterExpression e) {
        Expression base = visit(e.getBaseExpression());
        Expression filter = visit(e.getFilter());
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

    public Expression visit(LazyExpression e) {
        Expression base = visit(e.getBaseExpression());
        return LazyExpression.makeLazyExpression(base);
    }

    public Expression visit(BooleanExpression e) {
        final Expression[] operands = e.getOperands();
        Expression operand0 = visit(operands[0]);
        Expression operand1 = visit(operands[1]);
        return new BooleanExpression(operand0, e.getOperator(), operand1);
    }

    public Expression visit(Expression expr) {
        Expression result;
        if (expr instanceof DocumentSorter) {
            result = visit((DocumentSorter) expr);
        } else if (expr instanceof PathExpression) {
            result = visit((PathExpression) expr);
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
        } else if (expr instanceof LazyExpression) {
            result = visit((LazyExpression) expr);
        } else if (expr instanceof BooleanExpression) {
            result = visit((BooleanExpression) expr);
        } else {
            result = expr;
        }
        return result;
    }
}
