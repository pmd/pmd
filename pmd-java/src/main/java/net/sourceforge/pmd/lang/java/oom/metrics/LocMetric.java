/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.AbstractClassMetric;
import net.sourceforge.pmd.lang.java.oom.MetricOption;
import net.sourceforge.pmd.lang.java.oom.OperationMetric;
import net.sourceforge.pmd.lang.java.oom.PackageStats;

/**
 * Lines of Code. Equates the length in lines of code of the measured entity, counting everything including blank lines
 * and comments from the class declaration to the last closing brace. Import statements are not counted, for nested
 * classes to be comparable to outer ones.
 *
 * @author Clément Fournier
 * @see NcssMetric
 * @since June 2017
 */
public class LocMetric extends AbstractClassMetric implements OperationMetric {

    @Override
    public double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder, MetricOption options) {
        return node.getEndLine() - node.getBeginLine();
    }

    @Override
    public double computeFor(ASTMethodOrConstructorDeclaration node, PackageStats holder, MetricOption options) {
        if (node.isAbstract()) {
            return 1;
        }
        return node.getEndLine() - node.getBeginLine();
    }
}
