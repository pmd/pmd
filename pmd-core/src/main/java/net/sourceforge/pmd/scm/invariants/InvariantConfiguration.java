/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.invariants;

/**
 * Parameters of the invariant to be checked. To be specified on command line.
 *
 * @see com.beust.jcommander.Parameter
 */
public interface InvariantConfiguration {
    Invariant createChecker();
}
