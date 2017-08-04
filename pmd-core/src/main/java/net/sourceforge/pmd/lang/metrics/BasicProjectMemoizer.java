/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.QualifiableNode;
import net.sourceforge.pmd.lang.ast.QualifiedName;

/**
 * Simple implementation of a project memoizer. Memoizers are accessible in constant time, provided the QualifiedName's
 * hashCode is well distributed.
 *
 * @param <T> Type of type declaration nodes of the language
 * @param <O> Type of operation declaration nodes of the language
 *
 * @author Clément Fournier
 */
public class BasicProjectMemoizer<T extends QualifiableNode, O extends QualifiableNode> implements ProjectMemoizer<T, O> {

    private Map<QualifiedName, MetricMemoizer<T>> classes = new HashMap<>();
    private Map<QualifiedName, MetricMemoizer<O>> operations = new HashMap<>();


    @Override
    public void addClassMemoizer(QualifiedName qname) {
        classes.put(qname, new BasicMetricMemoizer<T>());
    }


    @Override
    public void addOperationMemoizer(QualifiedName qname) {
        operations.put(qname, new BasicMetricMemoizer<O>());
    }


    @Override
    public MetricMemoizer<O> getOperationMemoizer(QualifiedName qname) {
        return operations.get(qname);
    }


    @Override
    public MetricMemoizer<T> getClassMemoizer(QualifiedName qname) {
        return classes.get(qname);
    }
}
