/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import net.sf.saxon.expr.AxisExpression;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.RootExpression;
import net.sf.saxon.expr.Token;
import net.sf.saxon.expr.VennExpression;
import net.sf.saxon.om.Axis;

/**
 * Simple printer for saxon expressions. Might be useful for debugging / during development.
 *
 * <p>Example:
 * <pre>
 * ExpressionPrinter printer = new ExpressionPrinter();
 * printer.visit(query.xpathExpression.getInternalExpression());
 * </pre>
 */
public class ExpressionPrinter extends SaxonExprVisitor {
    private int depth = 0;

    private void print(String s) {
        for (int i = 0; i < depth; i++) {
            System.out.print("    ");
        }
        System.out.println(s);
    }

    @Override
    public Expression visit(AxisExpression e) {
        print("axis=" + Axis.axisName[e.getAxis()] + "(test=" + e.getNodeTest() + ")");
        return super.visit(e);
    }

    @Override
    public Expression visit(RootExpression e) {
        print("/");
        return super.visit(e);
    }

    @Override
    public Expression visit(VennExpression e) {
        print("venn=" + Token.tokens[e.getOperator()]);
        return super.visit(e);
    }

    @Override
    public Expression visit(Expression expr) {
        depth++;
        print(expr.getClass().getSimpleName());
        Expression result = super.visit(expr);
        depth--;
        return result;
    }
}
