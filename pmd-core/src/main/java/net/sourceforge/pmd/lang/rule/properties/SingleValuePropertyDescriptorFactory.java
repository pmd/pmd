/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorField;

/**
 * @author Clément Fournier
 */
public class SingleValuePropertyDescriptorFactory<T> extends BasicPropertyDescriptorFactory<T> {

    public SingleValuePropertyDescriptorFactory(Class<T> theValueType) {
        super(theValueType);
    }


    public SingleValuePropertyDescriptorFactory(Class<T> theValueType,
                                                Map<PropertyDescriptorField, Boolean> additionalFieldTypesByKey) {
        super(theValueType, additionalFieldTypesByKey);
    }


    @Override
    public boolean isMultiValue() {
        return false;
    }
}
