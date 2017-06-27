/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties.wrappers;

import net.sourceforge.pmd.SingleValuePropertyDescriptor;

/**
 * @author Clément Fournier
 */
public class SingleValuePropertyDescriptorWrapper<T> extends PropertyDescriptorWrapper<T>
    implements SingleValuePropertyDescriptor<T> {

    public SingleValuePropertyDescriptorWrapper(SingleValuePropertyDescriptor<T> propertyDescriptor) {
        super(propertyDescriptor);
    }


    @Override
    public Class<T> type() {
        return ((SingleValuePropertyDescriptor<T>) propertyDescriptor).type();
    }

}
