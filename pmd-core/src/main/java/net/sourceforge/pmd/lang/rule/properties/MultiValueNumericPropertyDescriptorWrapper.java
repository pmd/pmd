/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.List;

import net.sourceforge.pmd.MultiValuePropertyDescriptor;
import net.sourceforge.pmd.NumericPropertyDescriptor;

/**
 * @author Clément Fournier
 */
/* default */  class MultiValueNumericPropertyDescriptorWrapper<T>
    extends MultiValuePropertyDescriptorWrapper<T> implements NumericPropertyDescriptor<List<T>> {

    <D extends NumericPropertyDescriptor<List<T>> & MultiValuePropertyDescriptor<T>> MultiValueNumericPropertyDescriptorWrapper(D propertyDescriptor) {
        super(propertyDescriptor);
    }


    @Override
    public Number upperLimit() {
        return ((NumericPropertyDescriptor<T>) propertyDescriptor).upperLimit();
    }


    @Override
    public Number lowerLimit() {
        return ((NumericPropertyDescriptor<T>) propertyDescriptor).lowerLimit();
    }
}
