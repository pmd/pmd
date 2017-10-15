/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.builders;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public abstract class SingleNumericPropertyBuilder<V, T extends SingleNumericPropertyBuilder<V, T>>
        extends SingleValuePropertyBuilder<V, T> {


    protected V lowerLimit;
    protected V upperLimit;


    /**
     * Specify a minimum value.
     *
     * @param val Value
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public T min(V val) {
        this.lowerLimit = val;
        return (T) this;
    }


    /**
     * Specify a maximum value.
     *
     * @param val Value
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public T max(V val) {
        this.upperLimit = val;
        return (T) this;
    }
}
