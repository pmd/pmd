/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.oom.AbstractMetric;
import net.sourceforge.pmd.lang.java.oom.ClassMetric;
import net.sourceforge.pmd.lang.java.oom.MetricOption;
import net.sourceforge.pmd.lang.java.oom.Metrics.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.OperationMetric;
import net.sourceforge.pmd.lang.java.oom.PackageStats;
import net.sourceforge.pmd.lang.java.oom.metrics.cyclo.Accumulator;
import net.sourceforge.pmd.lang.java.oom.metrics.cyclo.CycloOperationVisitor;
import net.sourceforge.pmd.lang.java.oom.metrics.cyclo.CycloPathAwareOperationVisitor;

/**
 * Cyclomatic Complexity, a measure of the number of independent paths in a block of code. It is calculated by
 * counting decision points (control flow statements) in the block.
 *
 * <p>Standard rules to calculate CYCLO: +1 for every decision point, including case statements inside switches but not
 * including boolean operators.
 *
 * <p>{@code DO_NOT_COUNT_SWITCH_CASES} option: Switch labels do not count as decision points ---yet their blocks are
 * still analysed.
 *
 * <p>{@code COUNT_EXPRESSION_PATHS} option: Boolean expressions in control flow statements are broken down following
 * the number of boolean operators they use. Each different path counts as a decision point.
 *
 * @author Clément Fournier
 * @since June 2017
 */
public class CycloMetric extends AbstractMetric implements OperationMetric, ClassMetric {

    @Override
    public double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder, MetricOption options) {
        return sumMetricOnOperations(OperationMetricKey.CYCLO, node);
    }

    @Override
    public double computeFor(ASTMethodOrConstructorDeclaration node, PackageStats holder, MetricOption options) {

        CycloOperationVisitor visitor;
        if (options.equals(Option.DO_NOT_COUNT_SWITCH_CASES)) {
            visitor = new CycloOperationVisitor() {
                @Override
                public Object visit(ASTSwitchStatement node, Object data) {
                    ((Accumulator) data).addDecisionPoint();
                    visit((JavaNode) node, data);
                    return data;
                }
            };
        } else if (options.equals(Option.COUNT_EXPRESSION_PATHS)) {
            visitor = new CycloPathAwareOperationVisitor();
        } else {
            visitor = new CycloOperationVisitor();
        }

        Accumulator cyclo = (Accumulator) node.jjtAccept(visitor, new Accumulator());
        return cyclo.val;
    }

    /** Options for CYCLO. */
    public enum Option implements MetricOption {
        /** Count a switch as a single decision point (blocks are still explored). */
        DO_NOT_COUNT_SWITCH_CASES,
        /** Count the paths in boolean expressions as decision points. */
        COUNT_EXPRESSION_PATHS
    }
}
