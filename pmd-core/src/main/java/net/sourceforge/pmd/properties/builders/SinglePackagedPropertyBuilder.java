/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.builders;

import java.util.Arrays;
import java.util.Collection;


/**
 * @author Clément Fournier
 * @since 6.0.0
   * @deprecated see {@link net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilder}
 */
@Deprecated
public abstract class SinglePackagedPropertyBuilder<V, T extends SinglePackagedPropertyBuilder<V, T>>
    extends SingleValuePropertyBuilder<V, T> {

    protected String[] legalPackageNames;


    public SinglePackagedPropertyBuilder(String name) {
        super(name);
    }


    /**
     * Specify the allowed package prefixes.
     *
     * @param packs The package prefixes
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public T legalPackageNames(String... packs) {
        if (packs != null) {
            this.legalPackageNames = Arrays.copyOf(packs, packs.length);
        }
        return (T) this;
    }


    /**
     * Specify the allowed package prefixes.
     *
     * @param packs The package prefixes
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public T legalPackageNames(Collection<String> packs) {
        if (packs != null) {
            this.legalPackageNames = packs.toArray(new String[0]);
        }
        return (T) this;
    }

}
