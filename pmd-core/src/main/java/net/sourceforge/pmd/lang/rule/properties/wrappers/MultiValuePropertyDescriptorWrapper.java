/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties.wrappers;

import java.util.List;

import net.sourceforge.pmd.MultiValuePropertyDescriptor;

/**
 * @author Clément Fournier
 */
public class MultiValuePropertyDescriptorWrapper<T> extends PropertyDescriptorWrapper<List<T>>
    implements MultiValuePropertyDescriptor<T> {

    public MultiValuePropertyDescriptorWrapper(MultiValuePropertyDescriptor<T> propertyDescriptor) {
        super(propertyDescriptor);
    }


    @Override
    public Class<T> type() {
        return ((MultiValuePropertyDescriptor<T>) propertyDescriptor).type();
    }


    @Override
    public char multiValueDelimiter() {
        return ((MultiValuePropertyDescriptor<T>) propertyDescriptor).multiValueDelimiter();
    }


}
