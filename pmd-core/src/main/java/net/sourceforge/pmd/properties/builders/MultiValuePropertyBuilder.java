/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.builders;

import java.util.List;

import net.sourceforge.pmd.properties.MultiValuePropertyDescriptor;


/**
 * For multi-value properties.
 *
 * @param <V> Element type of the list
 * @param <T> Concrete type of the underlying builder
 */
public abstract class MultiValuePropertyBuilder<V, T extends MultiValuePropertyBuilder<V, T>>
        extends PropertyDescriptorBuilder<List<V>, T> {

    protected List<V> defaultValues;
    protected char multiValueDelimiter = MultiValuePropertyDescriptor.DEFAULT_DELIMITER;


    /**
     * Specify a default value.
     *
     * @param val List of values
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public T defalt(List<V> val) {
        this.defaultValues = val;
        return (T) this;
    }


    /**
     * Specify a delimiter character. By default it's {@link MultiValuePropertyDescriptor#DEFAULT_DELIMITER}, or {@link
     * MultiValuePropertyDescriptor#DEFAULT_NUMERIC_DELIMITER} for numeric properties.
     *
     * @param delim Delimiter
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public T delim(char delim) {
        this.multiValueDelimiter = delim;
        return (T) this;
    }
}
