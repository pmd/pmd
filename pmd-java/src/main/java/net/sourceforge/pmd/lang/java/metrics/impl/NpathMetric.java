/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;


import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.metrics.impl.visitors.DefaultNpathVisitor;
import net.sourceforge.pmd.lang.metrics.api.MetricVersion;

/**
 * NPath complexity is a measurement of the acyclic execution paths through a function. See Nejmeh, Communications of
 * the ACM Feb 1988 pp 188-200.
 *
 * @author Clément Fournier
 */
public class NpathMetric extends AbstractJavaOperationMetric {

    @Override
    public double computeFor(ASTMethodOrConstructorDeclaration node, MetricVersion version) {
        return (Integer) node.jjtAccept(new DefaultNpathVisitor(), null);
    }
}
