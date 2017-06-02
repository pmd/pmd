/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Generic signature mask.
 *
 * @author Clément Fournier
 */
public abstract class SigMask<T extends Signature> {
    /**
     * Visibility mask
     */
    protected Set<Signature.Visibility> visMask = new HashSet<>();

    /**
     * Clears the visibility mask and adds all parameters.
     *
     * @param visibilities The visibilities to add
     */
    public void setVisibilityMask(Signature.Visibility... visibilities) {
        visMask.clear();
        visMask.addAll(Arrays.asList(visibilities));
    }

    /**
     * Sets the mask to cover all visibilities.
     */
    public void setAllVisibility() {
        visMask.addAll(Arrays.asList(Signature.Visibility.values()));
    }

    /**
     * Removes all mentioned visibilities from the mask.
     *
     * @param visibilities The visibilities to remove
     */
    public void remove(Signature.Visibility... visibilities) {
        visMask.removeAll(Arrays.asList(visibilities));
    }

    /**
     * Returns true if the parameter is covered by this mask.
     *
     * @param sig The signature to test
     * @return True if the parameter is covered by this mask
     */
    public boolean covers(T sig) {
        return visMask.contains(sig.visibility);
    }

}
