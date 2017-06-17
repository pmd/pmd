/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.oom.AbstractClassMetric;
import net.sourceforge.pmd.lang.java.oom.keys.ClassMetric;
import net.sourceforge.pmd.lang.java.oom.keys.MetricOption;
import net.sourceforge.pmd.lang.java.oom.Metrics.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.keys.OperationMetric;
import net.sourceforge.pmd.lang.java.oom.PackageStats;
import net.sourceforge.pmd.lang.java.oom.metrics.cyclo.CycloOperationVisitor;
import net.sourceforge.pmd.lang.java.oom.metrics.cyclo.CycloPathAwareOperationVisitor;

/**
 * McCabe's Cyclomatic Complexity. Number of independent paths in a block of code. It is calculated by counting decision
 * points (control flow statements) in the block. [1]
 *
 * <p>Standard rules to calculate CYCLO: +1 for every control flow statement. The independent paths of boolean
 * expressions are not counted. Switch cases count as one, but not the switch itself ---the point is that a switch
 * should have the same complexity value as the equivalent series of {@code if} statements.
 *
 * <p>{@code COUNT_SWITCH_STATEMENTS} option: Switch statements count as 1 too.
 *
 * <p>{@code COUNT_EXPRESSION_PATHS} option: Boolean expressions in control flow statements are broken down following
 * the number of boolean operators they use. Each different path counts as a decision point.
 *
 * <p>[1] Lanza. Object-Oriented Metrics in Practice.
 *
 * @author Clément Fournier
 * @since June 2017
 */
public class CycloMetric extends AbstractClassMetric implements OperationMetric, ClassMetric {

    @Override
    public double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder, MetricOption option) {
        return sumMetricOverOperations(node, holder, OperationMetricKey.CYCLO, false);
    }

    @Override
    public double computeFor(ASTMethodOrConstructorDeclaration node, PackageStats holder, MetricOption option) {

        CycloOperationVisitor visitor;
        if (option.equals(Option.COUNT_SWITCH_STATEMENTS)) {
            visitor = new CycloOperationVisitor() {
                @Override
                public Object visit(ASTSwitchStatement node, Object data) {
                    ((MutableInt) data).increment();
                    visit((JavaNode) node, data);
                    return data;
                }
            };
        } else if (option.equals(Option.COUNT_EXPRESSION_PATHS)) {
            visitor = new CycloPathAwareOperationVisitor();
        } else {
            visitor = new CycloOperationVisitor();
        }

        MutableInt cyclo = (MutableInt) node.jjtAccept(visitor, new MutableInt(1));
        return (double) cyclo.getValue();
    }

    /** Options for CYCLO. */
    public enum Option implements MetricOption {
        /** Switch statements are counted as 1. */
        COUNT_SWITCH_STATEMENTS,
        /** Count the paths in boolean expressions as decision points. */
        COUNT_EXPRESSION_PATHS
    }
}
