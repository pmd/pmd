/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.Collections;

import net.sf.saxon.expr.AxisExpression;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.FilterExpression;
import net.sf.saxon.expr.RootExpression;
import net.sf.saxon.expr.SlashExpression;
import net.sf.saxon.om.AxisInfo;
import net.sf.saxon.pattern.AnyNodeTest;
import net.sf.saxon.pattern.NodeTest;

/**
 * Utilities to transform saxon expression trees.
 */
final class SaxonExprTransformations {

    private SaxonExprTransformations() {
        // utility class
    }

    private static final SaxonExprVisitor FILTER_HOISTER = new SaxonExprVisitor() {

        @Override
        public Expression visit(SlashExpression e) {
            Expression left = super.visit(e.getLhsExpression());
            Expression right = super.visit(e.getRhsExpression());
            if (right instanceof FilterExpression) {
                Expression middle = ((FilterExpression) right).getBase();
                Expression filter = ((FilterExpression) right).getFilter();
                return new FilterExpression(new SlashExpression(left, middle), filter);
            }
            return super.visit(e);
        }
    };

    private static final SaxonExprVisitor ROOT_REDUCER = new SaxonExprVisitor() {

        @Override
        public Expression visit(SlashExpression e) {
            Expression left = super.visit(e.getLhsExpression());
            Expression right = super.visit(e.getRhsExpression());

            if (right instanceof AxisExpression
                && ((AxisExpression) right).getAxis() == AxisInfo.CHILD
                && left instanceof SlashExpression) {

                Expression leftLeft = ((SlashExpression) left).getLhsExpression();
                Expression leftRight = ((SlashExpression) left).getRhsExpression();

                if (leftLeft instanceof RootExpression && leftRight instanceof AxisExpression) {
                    if (((AxisExpression) leftRight).getAxis() == AxisInfo.DESCENDANT_OR_SELF
                        && isAnyNode(((AxisExpression) leftRight).getNodeTest())) {
                        // ok!
                        left = leftLeft; // the root expression
                        right = new AxisExpression(AxisInfo.DESCENDANT, ((AxisExpression) right).getNodeTest());
                    }
                }
            }

            return new SlashExpression(left, right);
        }

        private boolean isAnyNode(NodeTest nodeTest) {
            return nodeTest == null || nodeTest instanceof AnyNodeTest;
        }
    };

    /**
     * Turn {@code a/(b[c])} into {@code (a/b)[c]}. This is to somewhat
     * normalize the expression as Saxon parses this inconsistently.
     */
    static Expression hoistFilters(Expression expression) {
        return FILTER_HOISTER.visit(expression);
    }

    /**
     * Turn {@code ((root)/descendant-or-self::node())/child::someTest}
     * into {@code ((root)/descendant::someTest)}. The latter is the pattern
     * detected by the rulechain analyser.
     */
    static Expression reduceRoot(Expression expression) {
        return ROOT_REDUCER.visit(expression);
    }

    /**
     * Splits a venn expression with the union operator into single expressions.
     *
     * <p>E.g. "//A | //B | //C" will result in 3 expressions "//A", "//B", and "//C".
     */
    static Iterable<Expression> splitUnions(Expression expr) {
        SplitUnions unions = new SplitUnions();
        unions.visit(expr);
        if (unions.getExpressions().isEmpty()) {
            return Collections.singletonList(expr);
        } else {
            return unions.getExpressions();
        }
    }

}
