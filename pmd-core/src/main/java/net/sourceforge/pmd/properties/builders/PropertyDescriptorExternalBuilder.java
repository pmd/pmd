/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.builders;

import java.util.Map;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyDescriptorField;


/**
 * Builds properties from a map of key value pairs, eg extracted from an XML element.
 *
 * @param <E> The type of values.
 * @deprecated see {@link net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilder}

 * @author Clément Fournier
 * @since 6.0.0
 * @deprecated The property XML API will not need this anymore
 */
@Deprecated
public interface PropertyDescriptorExternalBuilder<E> {


    /**
     * Whether this descriptor is multi-valued.
     *
     * @return True if this descriptor is multi-valued
     */
    boolean isMultiValue();


    /**
     * Type of the values of the descriptor, or component type if this descriptor is multi-valued.
     *
     * @return Type of the values
     */
    Class<?> valueType();


    /**
     * Builds a descriptor. The descriptor returned is tagged as built externally.
     *
     * @param fields Key value pairs
     *
     * @return A builder
     */
    PropertyDescriptor<E> build(Map<PropertyDescriptorField, String> fields);
}
