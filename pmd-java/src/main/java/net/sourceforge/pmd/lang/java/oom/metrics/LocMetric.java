/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.AbstractMetric;
import net.sourceforge.pmd.lang.java.oom.api.ClassMetric;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetric;

/**
 * Lines of Code. Equates the length in lines of code of the measured entity, counting everything including blank lines
 * and comments from the class declaration to the last closing brace. Import statements are not counted, for nested
 * classes to be comparable to outer ones.
 *
 * @author Clément Fournier
 * @see NcssMetric
 * @since June 2017
 */
public final class LocMetric extends AbstractMetric implements ClassMetric, OperationMetric {


    @Override
    public boolean supports(ASTAnyTypeDeclaration node) {
        return true;
    }

    @Override
    public boolean supports(ASTMethodOrConstructorDeclaration node) {
        return true;
    }

    @Override
    public double computeFor(ASTAnyTypeDeclaration node, MetricVersion version) {
        return node.getEndLine() - node.getBeginLine();
    }


    @Override
    public double computeFor(ASTMethodOrConstructorDeclaration node, MetricVersion version) {
        if (node.isAbstract()) {
            return 1;
        }
        return node.getEndLine() - node.getBeginLine();
    }

}
