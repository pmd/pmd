/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.builders;

import java.util.Arrays;


/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public abstract class MultiPackagedPropertyBuilder<V, T extends MultiPackagedPropertyBuilder<V, T>>
        extends MultiValuePropertyBuilder<V, T> {

    protected String[] legalPackageNames;


    @SuppressWarnings("unchecked")
    public T legalPackages(String[] packs) {
        if (packs != null) {
            this.legalPackageNames = Arrays.copyOf(packs, packs.length);
        }
        return (T) this;
    }

}
