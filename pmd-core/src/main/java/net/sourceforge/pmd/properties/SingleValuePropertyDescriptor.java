/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

/**
 * Specializes property descriptors for single valued descriptors. For this type of property, the return value of the
 * {@link #type()} method must be the class literal of the type parameter of the interface {@link PropertyDescriptor}.
 *
 * @param <T> The type of value this descriptor works with. Cannot be a list.
 * @author Clément Fournier
 * @since 6.0.0
 */
public interface SingleValuePropertyDescriptor<T> extends PropertyDescriptor<T> {

    @Override
    Class<T> type();
}
