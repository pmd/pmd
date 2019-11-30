/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.invariants;

/**
 * Checks some invariant about processing the source by the compiler
 */
public interface Invariant {
    boolean checkIsSatisfied() throws Exception;
}
