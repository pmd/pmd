/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

import java.util.List;

/**
 * This class represents a resolution result of some (possibly composite) name inside some context
 *
 * Usage of special interface instead of plain <code>List&lt;ModelicaDeclaration&gt;</code> allows returning some additional information
 * such as "layers" of resolved symbol (for example, these 10 symbols are obviously hidden, but these two are equally
 * relevant, thus introducing name clash). Moreover, more diagnostic information can be introduced in the future
 * without changing lots of source code.
 */
public interface ResolutionResult<A extends ResolvableEntity> {
    /**
     *  Returns declarations that are supposed to be taken by the Modelica compiler (normally, exactly one match).
     *
     * @return A declaration(s) to be used. Every result not consisting of exactly one declaration signifies
     * a possible error-severity issue in the source to be analysed.
     */
    List<A> getBestCandidates();

    /**
     * Returns declarations that are definitely hidden by some others.
     *
     * Non empty returned collection may signify either warning-grade issue in the source to be analysed
     * or just some peculiarities of the particular Modelica code design, it depends.
     *
     * @return A collections of definitely hidden resolution candidates
     */
    List<A> getHiddenCandidates();

    /**
     * Whether any resolution candidate exists.
     *
     * @return getBestCandidates().size != 0
     */
    boolean isUnresolved();

    /**
     *  Whether the symbol resolver found multiple equally relevant resolution candidates.
     *
     * @return getBestCandidates().size > 1
     */
    boolean isClashed();

    /**
     * Whether the symbol resolver found any symbol suspiciously hidden by other ones.
     *
     * @return getHiddenCandidates().size != 0
     */
    boolean hasHiddenResults();

    /**
     * During resolution, loops may occur. This leads to timing out the resolution and (possibly) partial results.
     *
     * This method may return `true` even if other ones return some non-empty result. It merely signifies that
     * the results may depend on particular limit on the number of resolution steps.
     *
     * @return Whether resolution was timed out
     */
    boolean wasTimedOut();
}
