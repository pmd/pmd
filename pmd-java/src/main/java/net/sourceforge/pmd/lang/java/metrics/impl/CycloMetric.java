/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.metrics.impl.internal.CycloVisitor;
import net.sourceforge.pmd.lang.metrics.MetricOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;


/**
 * Cyclomatic Complexity. See the <a href="https://pmd.github.io/latest/pmd_java_metrics_index.html">documentation site</a>.
 *
 * @author Clément Fournier
 * @since June 2017
 */
public final class CycloMetric extends AbstractJavaOperationMetric {


    // TODO:cf Cyclo should develop factorized boolean operators to count them


    @Override
    public double computeFor(MethodLikeNode node, MetricOptions options) {
        MutableInt cyclo = (MutableInt) node.jjtAccept(new CycloVisitor(options, node), new MutableInt(1));
        return (double) cyclo.getValue();
    }


    /**
     * Evaluates the number of paths through a boolean expression. This is the total number of {@code &&} and {@code ||}
     * operators appearing in the expression. This is used in the calculation of cyclomatic and n-path complexity.
     *
     * @param expr Expression to analyse
     *
     * @return The number of paths through the expression
     */
    public static int booleanExpressionComplexity(Node expr) {
        if (expr == null) {
            return 0;
        }

        List<ASTConditionalAndExpression> andNodes = expr.findDescendantsOfType(ASTConditionalAndExpression.class);
        List<ASTConditionalOrExpression> orNodes = expr.findDescendantsOfType(ASTConditionalOrExpression.class);

        int complexity = 0;

        if (expr instanceof ASTConditionalOrExpression || expr instanceof ASTConditionalAndExpression) {
            complexity++;
        }

        for (ASTConditionalOrExpression element : orNodes) {
            complexity += element.getNumChildren() - 1;
        }

        for (ASTConditionalAndExpression element : andNodes) {
            complexity += element.getNumChildren() - 1;
        }

        return complexity;
    }


    /** Options for CYCLO. */
    public enum CycloOption implements MetricOption {
        /** Do not count the paths in boolean expressions as decision points. */
        IGNORE_BOOLEAN_PATHS("ignoreBooleanPaths"),
        /** Consider assert statements. */
        CONSIDER_ASSERT("considerAssert");

        private final String vName;


        CycloOption(String valueName) {
            this.vName = valueName;
        }


        @Override
        public String valueName() {
            return vName;
        }
    }

}
