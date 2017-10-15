/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.builders;

import net.sourceforge.pmd.properties.MultiValuePropertyDescriptor;


/**
 * For multi-value numeric properties.
 *
 * @param <V> Element type of the list
 * @param <T> Concrete type of the underlying builder
 */
public abstract class MultiNumericPropertyBuilder<V, T extends MultiNumericPropertyBuilder<V, T>>
        extends MultiValuePropertyBuilder<V, T> {


    protected V lowerLimit;
    protected V upperLimit;


    protected MultiNumericPropertyBuilder() {
        multiValueDelimiter = MultiValuePropertyDescriptor.DEFAULT_NUMERIC_DELIMITER;
    }


    /**
     * Specify a minimum value.
     *
     * @param val Value
     *
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
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public T max(V val) {
        this.upperLimit = val;
        return (T) this;
    }

}
