/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.metrics;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.metrics.Signature.Visibility;

/**
 * @author Clément Fournier
 *
 */
public abstract class SigMask<T extends Signature> {
    protected Set<Visibility> visMask = new HashSet<>();
    
    public void setVisibilityMask(Visibility... visibilities) {
        visMask.clear();
        visMask.addAll(Arrays.asList(visibilities));
    }
    
    public void setAllVisibility() {
        visMask.addAll(Arrays.asList(Visibility.ALL));
    }
    
    public void remove(Visibility... visibilities) {
        visMask.removeAll(Arrays.asList(visibilities));
    }

    public boolean covers(T sig) {
        return visMask.contains(sig.visibility);
    }
    
}
