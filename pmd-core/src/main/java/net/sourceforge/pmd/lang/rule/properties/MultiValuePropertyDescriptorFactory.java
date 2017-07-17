/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.AbstractPropertyDescriptorFactory;
import net.sourceforge.pmd.MultiValuePropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptorField;

/**
 * Concrete implementation of a property descriptor factory for multi valued properties.
 *
 * @param <T> Type of the property
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public abstract class MultiValuePropertyDescriptorFactory<T> extends AbstractPropertyDescriptorFactory<List<T>> {

    public MultiValuePropertyDescriptorFactory(Class<T> theValueType) {
        super(theValueType);
    }


    public MultiValuePropertyDescriptorFactory(Class<T> theValueType,
                                               Map<PropertyDescriptorField, Boolean> additionalFieldTypesByKey) {
        super(theValueType, additionalFieldTypesByKey);
    }


    @Override
    protected abstract MultiValuePropertyDescriptor<T> createWith(Map<PropertyDescriptorField, String> valuesById,
                                                                  boolean isDefinedExternally);


    @Override
    public boolean isMultiValue() {
        return true;
    }
}
