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
import net.sourceforge.pmd.lang.java.oom.Metrics.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.OperationMetric;
import net.sourceforge.pmd.lang.java.oom.PackageStats;
import net.sourceforge.pmd.lang.java.oom.metrics.StdCycloMetric.Accumulator;

/**
 * @author Clément Fournier
 */
public class ModifiedCycloMetric extends AbstractMetric implements OperationMetric, ClassMetric {

    @Override
    public double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder) {
        return sumMetricOnOperations(OperationMetricKey.CYCLO, node);
    }

    @Override
    public double computeFor(ASTMethodOrConstructorDeclaration node, PackageStats holder) {
        Accumulator cyclo = (Accumulator) node.jjtAccept(new OperationVisitor(), new Accumulator());
        return cyclo.val;
    }

    protected class OperationVisitor extends StdCycloMetric.OperationVisitor {
        @Override
        public Object visit(ASTSwitchStatement node, Object data) {
            ((Accumulator) data).addDecisionPoint();
            visit((JavaNode) node, data);
            return data;
        }
    }
}
