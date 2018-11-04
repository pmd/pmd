/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.sourceforge.pmd.properties.MultiValuePropertyDescriptor;


/**
 * For multi-value properties.
 *
 * @param <V> Element type of the list
 * @param <T> Concrete type of the underlying builder
 *
 */
// @Deprecated // will be rewritten in the next PR and placed in the properties package,
// since there will be no need for a separate package
public abstract class MultiValuePropertyBuilder<V, T extends MultiValuePropertyBuilder<V, T>>
    extends PropertyDescriptorBuilder<List<V>, T> {

    protected List<V> defaultValues;
    protected char multiValueDelimiter = MultiValuePropertyDescriptor.DEFAULT_DELIMITER;


    protected MultiValuePropertyBuilder(String name) {
        super(name);
    }


    /**
     * Specify a default value.
     *
     * @param val List of values
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public T defaultValues(Collection<? extends V> val) {
        this.defaultValues = new ArrayList<>(val);
        return (T) this;
    }


    /**
     * Specify default values.
     *
     * @param val List of values
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public T defaultValues(V... val) {
        this.defaultValues = Arrays.asList(val);
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
