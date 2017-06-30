/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.SingleValuePropertyDescriptor;

/**
 * @author Clément Fournier
 */
/* default */  class SingleValuePropertyDescriptorWrapper<T> extends PropertyDescriptorWrapper<T>
    implements SingleValuePropertyDescriptor<T> {

    SingleValuePropertyDescriptorWrapper(SingleValuePropertyDescriptor<T> propertyDescriptor) {
        super(propertyDescriptor);
    }


    @Override
    public Class<T> type() {
        return ((SingleValuePropertyDescriptor<T>) propertyDescriptor).type();
    }

}
