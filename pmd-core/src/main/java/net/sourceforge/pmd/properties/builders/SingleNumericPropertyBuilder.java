/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.builders;

/**
 * @author Clément Fournier
 * @since 6.0.0
 * @deprecated see {@link net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilder}
 */
@Deprecated
public abstract class SingleNumericPropertyBuilder<V, T extends SingleNumericPropertyBuilder<V, T>>
    extends SingleValuePropertyBuilder<V, T> {


    protected V lowerLimit;
    protected V upperLimit;


    public SingleNumericPropertyBuilder(String name) {
        super(name);
    }


    /**
     * Specify the range of acceptable values.
     *
     * @param min Lower bound
     * @param max Upper bound
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public T range(V min, V max) {
        this.lowerLimit = min;
        this.upperLimit = max;
        return (T) this;
    }

}
