/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitor;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.metrics.impl.visitors.CycloPathUnawareOperationVisitor;
import net.sourceforge.pmd.lang.java.metrics.impl.visitors.StandardCycloVisitor;
import net.sourceforge.pmd.lang.metrics.api.MetricVersion;
import net.sourceforge.pmd.lang.metrics.api.ResultOption;

/**
 * McCabe's Cyclomatic Complexity. Number of independent paths through a block of code [1, 2]. Formally, given that the
 * control flow graph of the block has n vertices, e edges and p connected components, the Cyclomatic complexity of the
 * block is given by {@code CYCLO = e - n + 2p} [2]. In practice it can be calculated by counting control flow
 * statements following the standard rules given below.
 *
 * <p>The standard version of the metric complies with McCabe's original definition [3]:
 *
 * <ul>
 * <li>+1 for every control flow statement ({@code if, case, catch, throw, do, while, for, break, continue}) and
 * conditional expression ({@code ? : }). Notice switch cases count as one, but not the switch itself: the point is that
 * a switch should have the same complexity value as the equivalent series of {@code if} statements.
 * <li>{@code else}, {@code finally} and {@code default} don't count;
 * <li>+1 for every boolean operator ({@code &&, ||}) in the guard condition of a control flow statement. That's because
 * Java has short-circuit evaluation semantics for boolean operators, which makes every boolean operator kind of a
 * control flow statement in itself.
 * </ul>
 *
 * <p>Version {@link CycloVersion#IGNORE_BOOLEAN_PATHS}: Boolean operators are not counted, which means that empty
 * fall-through cases in {@code switch} statements are not counted as well.
 *
 * <p>References:
 * <ul>
 * <li> [1] Lanza, Object-Oriented Metrics in Practice, 2005.
 * <li> [2] McCabe, A Complexity Measure, in Proceedings of the 2nd ICSE (1976).
 * <li> [3] <a href="https://docs.sonarqube.org/display/SONAR/Metrics+-+Complexity">Sonarqube online documentation</a>
 * </ul>
 *
 * @author Clément Fournier
 * @since June 2017
 */
public final class CycloMetric {

    // TODO:cf Cyclo should develop factorized boolean operators to count them

    /** Variants of CYCLO. */
    public enum CycloVersion implements MetricVersion {
        /** Do not count the paths in boolean expressions as decision points. */
        IGNORE_BOOLEAN_PATHS
    }

    public static final class CycloOperationMetric extends AbstractJavaOperationMetric {

        @Override
        public double computeFor(ASTMethodOrConstructorDeclaration node, MetricVersion version) {

            JavaParserVisitor visitor = (CycloVersion.IGNORE_BOOLEAN_PATHS == version)
                                        ? new CycloPathUnawareOperationVisitor()
                                        : new StandardCycloVisitor();

            MutableInt cyclo = (MutableInt) node.jjtAccept(visitor, new MutableInt(1));
            return (double) cyclo.getValue();
        }
    }

    public static final class CycloClassMetric extends AbstractJavaClassMetric {

        @Override
        public double computeFor(ASTAnyTypeDeclaration node, MetricVersion version) {
            return 1 + JavaMetrics.get(JavaOperationMetricKey.CYCLO, node, version, ResultOption.AVERAGE);
        }
    }
}
