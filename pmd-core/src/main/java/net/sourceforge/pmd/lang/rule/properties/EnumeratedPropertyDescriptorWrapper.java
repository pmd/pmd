/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.EnumeratedPropertyDescriptor;

/**
 * @author Clément Fournier
 */
/* default */ class EnumeratedPropertyDescriptorWrapper<E, T> extends PropertyDescriptorWrapper<T>
    implements EnumeratedPropertyDescriptor<E, T> {

    EnumeratedPropertyDescriptorWrapper(EnumeratedPropertyDescriptor<E, T> propertyDescriptor) {
        super(propertyDescriptor);
    }


    @Override
    public Map<String, E> mappings() {
        return ((EnumeratedPropertyDescriptor<E, T>) propertyDescriptor).mappings();
    }


}
