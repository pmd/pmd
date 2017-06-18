/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.AbstractClassMetric;
import net.sourceforge.pmd.lang.java.oom.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.interfaces.ClassMetric;
import net.sourceforge.pmd.lang.java.oom.interfaces.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.interfaces.OperationMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.cyclo.CycloPathUnawareOperationVisitor;
import net.sourceforge.pmd.lang.java.oom.metrics.cyclo.CycloVisitor;
import net.sourceforge.pmd.lang.java.oom.metrics.cyclo.StandardCycloVisitor;

/**
 * McCabe's Cyclomatic Complexity. Number of independent paths through a block of code. Formally, given that the control
 * flow graph of a method has n edges, m nodes and p connected components, the Cyclomatic complexity of the method is
 * given by CYCLO = n - m + 2p. In practice it can be calculated by counting control flow statements following the
 * standard rules given below.
 *
 * <p>The standard version of the metric complies with McCabe's original definition:
 *
 * <ul>
 * <li>+1 for every control flow statement ({@code if, case, catch, finally, do, while, for, break, continue}) and
 * conditional expression ({@code ? : }). Notice switch cases count as one, but not the switch itself: the point is
 * that a switch should have the same complexity value as the equivalent series of {@code if} statements.
 * <li>{@code else} and {@code default} don't count;
 * <li>+1 for every boolean operator in the guard condition of a control flow statement. That's because Java has
 * short-circuit evaluation semantics for boolean operators, which makes every boolean operator kind of a control flow
 * statement in itself.
 * </ul>
 *
 * <p> Version {@link Version#DO_NOT_COUNT_EXPRESSION_PATHS}: Boolean operators are not counted, which means that empty
 * fall-through cases in {@code switch} statements are not counted as well.
 *
 * <p>[1] Lanza. Object-Oriented Metrics in Practice.
 *
 * @author Clément Fournier
 * @since June 2017
 */
public class CycloMetric extends AbstractClassMetric implements OperationMetric, ClassMetric {

    @Override
    public double computeFor(ASTClassOrInterfaceDeclaration node, MetricVersion version) {
        return sumMetricOverOperations(node, getTopLevelPackageStats(), OperationMetricKey.CYCLO, false);
    }

    @Override
    public double computeFor(ASTMethodOrConstructorDeclaration node, MetricVersion version) {

        CycloVisitor visitor = (Version.DO_NOT_COUNT_EXPRESSION_PATHS.equals(version))
                               ? new CycloPathUnawareOperationVisitor()
                               : new StandardCycloVisitor();

        MutableInt cyclo = (MutableInt) node.jjtAccept(visitor, new MutableInt(1));
        return (double) cyclo.getValue();
    }

    /** Variants of CYCLO. */
    public enum Version implements MetricVersion {
        /** Do not count the paths in boolean expressions as decision points. */
        DO_NOT_COUNT_EXPRESSION_PATHS
    }

}
