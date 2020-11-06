/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import net.sourceforge.pmd.lang.ast.QualifiableNode;
import net.sourceforge.pmd.lang.ast.QualifiedName;

/**
 * Object storing the memoizers of the analysed project. This object should ideally be kept separate from the
 * SignatureMatcher if there is one. A base implementation is available, see {@link BasicProjectMemoizer}.
 *
 * <p>Memoizers need not be all kept, in fact, only those who refer to operations or classes defined in
 * the analysed file are still relevant.
 *
 * @param <T> Type of type declaration nodes of the language
 * @param <O> Type of operation declaration nodes of the language
 *
 * @author Clément Fournier
 * @since 6.0.0
 * @deprecated See package description
 */
@Deprecated
public interface ProjectMemoizer<T extends QualifiableNode, O extends QualifiableNode> {

    /**
     * Gets the operation metric memoizer corresponding to the qualified name.
     *
     * @param qname The qualified name of the operation to fetch
     *
     * @return The correct memoizer, or null if it wasn't found
     */
    MetricMemoizer<O> getOperationMemoizer(QualifiedName qname);


    /**
     * Gets the class metric memoizer corresponding to the qualified name.
     *
     * @param qname The qualified name of the class to fetch
     *
     * @return The correct memoizer, or null if it wasn't found
     */
    MetricMemoizer<T> getClassMemoizer(QualifiedName qname);

}
