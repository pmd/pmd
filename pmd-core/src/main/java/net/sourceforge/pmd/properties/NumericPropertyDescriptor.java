/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

/**
 * Defines a descriptor type whose instance values are required to lie within specified upper and lower limits.
 *
 * @param <T> type of the property value
 *
 * @deprecated Will be removed with 7.0.0. In the future this interface won't exist,
 * but numeric properties will still be around
 * @author Brian Remedios
 * @author Clément Fournier
 */
@Deprecated
public interface NumericPropertyDescriptor<T> extends PropertyDescriptor<T> {

    /**
     * Returns the maximum value that instances of the property can have.
     *
     * @return Number
     */
    Number upperLimit();


    /**
     * Returns the minimum value that instances of the property can have.
     *
     * @return Number
     */
    Number lowerLimit();
}
