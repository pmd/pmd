/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.util.Map;

/**
 * A factory to create {@link PropertyDescriptor}s based on a map of values.
 * 
 * @author Brian Remedios
 */
public interface PropertyDescriptorFactory {

    /**
     * The type of the value of the {@link PropertyDescriptor} created by this
     * factory.
     * 
     * @return the type of the value.
     */
    Class<?> valueType();

    /**
     * Denote the identifiers of the expected fields paired with booleans
     * denoting whether they are required (non-null) or not.
     * 
     * @return Map
     */
    Map<String, Boolean> expectedFields();

    /**
     * Create a property descriptor of the appropriate type using the values
     * provided.
     * 
     * @param valuesById the map of values
     * @return a new and initialized {@link PropertyDescriptor}
     */
    PropertyDescriptor<?> createWith(Map<String, String> valuesById);
}
