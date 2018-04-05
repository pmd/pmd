/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.signature;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.metrics.signature.ApexSignature.Visibility;

/**
 * @author Clément Fournier
 */
public class ApexOperationSigMask {

    private Set<Visibility> visMask = EnumSet.allOf(Visibility.class);


    /**
     * Sets the mask to cover all visibilities.
     */
    public void coverAllVisibilities() {
        visMask.addAll(Arrays.asList(Visibility.values()));
    }


    /**
     * Restricts the visibilities covered by the mask to the parameters.
     *
     * @param visibilities The visibilities to cover
     */
    public void restrictVisibilitiesTo(Visibility... visibilities) {
        visMask.clear();
        visMask.addAll(Arrays.asList(visibilities));
    }


    /**
     * Forbid all mentioned visibilities.
     *
     * @param visibilities The visibilities to forbid
     */
    public void forbid(Visibility... visibilities) {
        visMask.removeAll(Arrays.asList(visibilities));
    }


    /**
     * Returns true if the parameter is covered by this mask.
     *
     * @param sig The signature to test.
     *
     * @return True if the parameter is covered by this mask
     */
    public boolean covers(ApexOperationSignature sig) {
        return visMask.contains(sig.visibility);
    }


}
