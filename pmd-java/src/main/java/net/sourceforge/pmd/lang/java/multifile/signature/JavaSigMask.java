/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile.signature;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.multifile.signature.JavaSignature.Visibility;
import net.sourceforge.pmd.lang.metrics.SigMask;

/**
 * Generic signature mask.
 *
 * @param <T> The type of Signature to handle.
 *
 * @author Clément Fournier
 */
public abstract class JavaSigMask<T extends JavaSignature<?>> implements SigMask<T> {

    /** Visibility mask. */
    private Set<JavaSignature.Visibility> visMask = EnumSet.allOf(Visibility.class);


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


    @Override
    public boolean covers(T sig) {
        return visMask.contains(sig.visibility);
    }
}
