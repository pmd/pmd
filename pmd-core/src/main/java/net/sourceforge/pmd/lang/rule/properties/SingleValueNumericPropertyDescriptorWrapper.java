/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.NumericPropertyDescriptor;
import net.sourceforge.pmd.SingleValuePropertyDescriptor;

/**
 * @author Clément Fournier
 */
/* default */ class SingleValueNumericPropertyDescriptorWrapper<T extends Number>
    extends SingleValuePropertyDescriptorWrapper<T> implements NumericPropertyDescriptor<T> {

    <D extends NumericPropertyDescriptor<T> & SingleValuePropertyDescriptor<T>> SingleValueNumericPropertyDescriptorWrapper(D propertyDescriptor) {
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
