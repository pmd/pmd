/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

/**
 * Specializes property descriptors for single valued descriptors. For this type of property, the return value of the
 *
 * @param <T> The type of value this descriptor works with. Cannot be a list.
 *
 * @author Clément Fournier
 * @since 6.0.0
 *
 * @deprecated The hard divide between multi- and single-value properties will be removed with 7.0.0
 */
@Deprecated
public interface SingleValuePropertyDescriptor<T> extends PropertyDescriptor<T> {

}
