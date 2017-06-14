/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.AbstractMetric;
import net.sourceforge.pmd.lang.java.oom.ClassMetric;
import net.sourceforge.pmd.lang.java.oom.MetricOption;
import net.sourceforge.pmd.lang.java.oom.OperationMetric;
import net.sourceforge.pmd.lang.java.oom.PackageStats;

/**
 * Lines Of Code metric. Simply equates the length in lines of code of the measured entity.
 *
 * @author Clément Fournier
 */
public class LocMetric extends AbstractMetric implements ClassMetric, OperationMetric {

    @Override
    public double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder, MetricOption options) {
        return node.getEndLine() - node.getBeginLine();
    }

    @Override
    public double computeFor(ASTMethodOrConstructorDeclaration node, PackageStats holder, MetricOption options) {
        if (!isSupported(node)) {
            return Double.NaN;
        }
        return node.getEndLine() - node.getBeginLine();
    }
}
