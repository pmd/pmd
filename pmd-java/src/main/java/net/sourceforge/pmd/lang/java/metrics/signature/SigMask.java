/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.signature;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Generic signature mask.
 *
 * @param <T> The type of Signature to handle.
 *
 * @author Clément Fournier
 */
public abstract class SigMask<T extends JavaSignature> {

    /** Visibility mask. */
    private Set<JavaSignature.Visibility> visMask = new HashSet<>();


    public SigMask() {
        coverAllVisibilities();
    }


    /**
     * Sets the mask to cover all visibilities.
     */
    public void coverAllVisibilities() {
        visMask.addAll(Arrays.asList(JavaSignature.Visibility.values()));
    }


    /**
     * Restricts the visibilities covered by the mask to the parameters.
     *
     * @param visibilities The visibilities to cover
     */
    public void restrictVisibilitiesTo(JavaSignature.Visibility... visibilities) {
        visMask.clear();
        visMask.addAll(Arrays.asList(visibilities));
    }


    /**
     * Forbid all mentioned visibilities.
     *
     * @param visibilities The visibilities to forbid
     */
    public void forbid(JavaSignature.Visibility... visibilities) {
        visMask.removeAll(Arrays.asList(visibilities));
    }


    /**
     * Returns true if the parameter is covered by this mask.
     *
     * @param sig The signature to test.
     *
     * @return True if the parameter is covered by this mask
     */
    public boolean covers(T sig) {
        return visMask.contains(sig.visibility);
    }
}
