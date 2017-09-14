/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Map;

/**
 * Concrete implementation of a property descriptor factory for single valued properties.
 *
 * @param <T> Type of the property
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public abstract class SingleValuePropertyDescriptorFactory<T> extends AbstractPropertyDescriptorFactory<T> {

    /* default */ SingleValuePropertyDescriptorFactory(Class<T> theValueType) {
        super(theValueType);
    }


    /* default */ SingleValuePropertyDescriptorFactory(Class<T> theValueType,
                                                       Map<PropertyDescriptorField, Boolean> additionalFieldTypesByKey) {
        super(theValueType, additionalFieldTypesByKey);
    }


    @Override
    public abstract SingleValuePropertyDescriptor<T> createWith(Map<PropertyDescriptorField, String> valuesById,
                                                                boolean isDefinedExternally);


    @Override
    public final boolean isMultiValue() {
        return false;
    }
}
