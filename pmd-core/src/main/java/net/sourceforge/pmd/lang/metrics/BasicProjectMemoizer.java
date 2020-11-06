/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.Map;
import java.util.WeakHashMap;

import net.sourceforge.pmd.lang.ast.QualifiableNode;
import net.sourceforge.pmd.lang.ast.QualifiedName;

/**
 * Simple implementation of a project memoizer. Memoizers are accessible in constant time, provided the QualifiedName's
 * hashCode is well distributed.
 *
 * <p>This implementation takes care of recollecting irrelevant memoizers by storing them in {@link WeakHashMap}.
 *
 * @param <T> Type of type declaration nodes of the language
 * @param <O> Type of operation declaration nodes of the language
 *
 * @author Clément Fournier
 * @since 6.0.0
 *
 * @deprecated See package description
 */
@Deprecated
public abstract class BasicProjectMemoizer<T extends QualifiableNode, O extends QualifiableNode>
    implements ProjectMemoizer<T, O> {

    private Map<QualifiedName, MetricMemoizer<T>> classes = new WeakHashMap<>();
    private Map<QualifiedName, MetricMemoizer<O>> operations = new WeakHashMap<>();

    private final Object classesSynchronizer = new Object();
    private final Object operationsSynchronizer = new Object();

    /** Clears all memoizers. Used for tests. */
    public void reset() {
        classes.clear();
        operations.clear();
    }


    @Override
    public MetricMemoizer<O> getOperationMemoizer(QualifiedName qname) {
        synchronized (operationsSynchronizer) {
            if (!operations.containsKey(qname)) {
                operations.put(qname, new BasicMetricMemoizer<O>());
            }
        }

        return operations.get(qname);
    }


    @Override
    public MetricMemoizer<T> getClassMemoizer(QualifiedName qname) {
        synchronized (classesSynchronizer) {
            if (!classes.containsKey(qname)) {
                classes.put(qname, new BasicMetricMemoizer<T>());
            }
        }

        return classes.get(qname);
    }
}
