/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.builders;

import java.util.Arrays;


/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public abstract class SinglePackagedPropertyBuilder<V, T extends SinglePackagedPropertyBuilder<V, T>>
        extends SingleValuePropertyBuilder<V, T> {

    protected String[] legalPackageNames;


    @SuppressWarnings("unchecked")
    public T legalPackageNames(String[] packs) {
        this.legalPackageNames = Arrays.copyOf(packs, packs.length);
        return (T) this;
    }

}
