/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics.internal;

import net.sourceforge.pmd.lang.ast.QualifiableNode;
import net.sourceforge.pmd.lang.ast.QualifiedName;
import net.sourceforge.pmd.lang.metrics.MetricMemoizer;
import net.sourceforge.pmd.lang.metrics.ProjectMemoizer;


/**
 * Memoizes nothing.
 *
 * @author Clément Fournier
 * @since 6.11.0
 */
public final class DummyProjectMemoizer<T extends QualifiableNode, O extends QualifiableNode> implements ProjectMemoizer<T, O> {

    private static final DummyProjectMemoizer<? extends QualifiableNode, ? extends QualifiableNode> INSTANCE = new DummyProjectMemoizer<>();


    private DummyProjectMemoizer() {

    }


    @Override
    public MetricMemoizer<O> getOperationMemoizer(QualifiedName qname) {
        return DummyMetricMemoizer.getInstance();
    }


    @Override
    public MetricMemoizer<T> getClassMemoizer(QualifiedName qname) {
        return DummyMetricMemoizer.getInstance();
    }


    @SuppressWarnings("unchecked")
    public static <T extends QualifiableNode, O extends QualifiableNode> DummyProjectMemoizer<T, O> getInstance() {
        return (DummyProjectMemoizer<T, O>) INSTANCE;
    }
}
