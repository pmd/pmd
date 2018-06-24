/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

/**
 * Generic signature mask.
 *
 * @param <T> Type of signature this mask handles
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public interface SigMask<T extends Signature<?>> {

    /**
     * Returns true if the parameter is covered by this mask.
     *
     * @param sig The signature to test.
     *
     * @return True if the parameter is covered by this mask
     */
    boolean covers(T sig);

}
