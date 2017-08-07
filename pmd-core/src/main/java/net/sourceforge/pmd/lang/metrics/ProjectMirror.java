/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import net.sourceforge.pmd.lang.ast.QualifiableNode;
import net.sourceforge.pmd.lang.ast.QualifiedName;
import net.sourceforge.pmd.lang.ast.SignedNode;

/**
 * Object storing the statistics and memoizers of the analysed project, like PackageStats for Java. These are the entry
 * point for signature matching requests. If retrieving eg an operation stats is expensive, consider implementing a
 * cache.
 *
 * <p>Language specific implementations should implement some signature matching utilities for metrics to use. The
 * details of how the mirror and its subcomponents are built must be kept out of the interfaces and visible only to the
 * language specific metric package. Metric implementations, even standard ones, should not have access to it.
 *
 * <p>While classes and operations are widespread and vary little in form across (class based at least) object-oriented
 * languages, the structure of a project is very language specific. For example, while an intuitive way to represent a
 * Java project is with a package tree, Apex has no package system. We consider here that there's no point providing
 * base implementations, so we use an interface. You could even implement it with a bunch of maps, which may yield good
 * results! // TODO:cf investigate that
 *
 * @param <T> Type of type declaration nodes of the language
 * @param <O> Type of operation declaration nodes of the language
 *
 * @author Clément Fournier
 */
public interface ProjectMirror<T extends QualifiableNode, O extends SignedNode<O> & QualifiableNode> {

    /**
     * Gets the operation metric memoizer corresponding to the qualified name.
     *
     * @param qname The qualified name of the operation to fetch
     *
     * @return The correct memoizer, or null if it wasn't found
     */
    MetricMemoizer<O> getOperationStats(QualifiedName qname);


    /**
     * Gets the class metric memoizer corresponding to the qualified name.
     *
     * @param qname The qualified name of the class to fetch
     *
     * @return The correct memoizer, or null if it wasn't found
     */
    MetricMemoizer<T> getClassStats(QualifiedName qname);

}
