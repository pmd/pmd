/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Map;
import java.util.Set;

/**
 * A factory to create {@link PropertyDescriptor}s based on a map of values.
 *
 * @param <T> The type of values property descriptor returned by this factory. This can be a list.
 *
 * @author Brian Remedios
 * @version Refactored July 2017 (6.0.0)
 */
public interface PropertyDescriptorFactory<T> {

    /**
     * The type of the value of the {@link PropertyDescriptor} created by this
     * factory.
     *
     * @return The type of the value.
     */
    Class<?> valueType();


    /**
     * Returns true if the built property descriptor is multi-valued.
     *
     * @return True if the built property descriptor is multi-valued.
     */
    boolean isMultiValue();


    /**
     * Denote the identifiers of all fields that contribute to building
     * this descriptor. Control of the required fields is performed
     * inside the factory.
     *
     * @return A set of field identifiers
     */
    Set<PropertyDescriptorField> expectableFields();


    /**
     * Create a property descriptor of the appropriate type using the values
     * provided.
     *
     * @param valuesById the map of values
     *
     * @return A new and initialized {@link PropertyDescriptor}
     */
    PropertyDescriptor<T> createWith(Map<PropertyDescriptorField, String> valuesById);
}
