/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.builders;

import java.util.Arrays;


/**
 * @author Clément Fournier
 * @since 6.0.0
 *
 * @deprecated see {@link net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilder}
 */
@Deprecated
public abstract class MultiPackagedPropertyBuilder<V, T extends MultiPackagedPropertyBuilder<V, T>>
        extends MultiValuePropertyBuilder<V, T> {

    protected String[] legalPackageNames;


    protected MultiPackagedPropertyBuilder(String name) {
        super(name);
    }


    @SuppressWarnings("unchecked")
    public T legalPackages(String[] packs) {
        if (packs != null) {
            this.legalPackageNames = Arrays.copyOf(packs, packs.length);
        }
        return (T) this;
    }

}
