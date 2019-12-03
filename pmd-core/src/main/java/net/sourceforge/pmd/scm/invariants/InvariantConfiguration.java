/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.invariants;

/**
 * Parameters of the invariant to be checked. To be specified on command line.
 *
 * @see com.beust.jcommander.JCommander
 * @see com.beust.jcommander.Parameter
 */
public interface InvariantConfiguration {
    /**
     * Creates an invariant checker according to the current state of this object.
     *
     * Future changes to this object do not affect the created checker.
     */
    Invariant createChecker();
}
