/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.Map;

/**
 * A factory to create {@link PropertyDescriptor}s based on a map of values.
 *
 * @param <T> the type of values property descriptor returned by this factory. This can be a list.
 *
 * @author Brian Remedios
 */
public interface PropertyDescriptorFactory<T> {

    /**
     * The type of the value of the {@link PropertyDescriptor} created by this
     * factory.
     *
     * @return the type of the value.
     */
    Class<?> valueType();


    /**
     * Returns true if the wrapped property descriptor is multi-valued.
     *
     * @return true if the wrapped property descriptor is multi-valued.
     */
    boolean isMultiValue();


    /**
     * Denote the identifiers of the expected fields paired with booleans
     * denoting whether they are required (non-null) or not.
     *
     * @return Map
     */
    Map<PropertyDescriptorField, Boolean> expectedFields();


    /**
     * Create a property descriptor of the appropriate type using the values
     * provided.
     *
     * @param valuesById the map of values
     *
     * @return a new and initialized {@link PropertyDescriptor}
     */
    PropertyDescriptor<T> createWith(Map<PropertyDescriptorField, String> valuesById);
}
