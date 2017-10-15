/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

/**
 * Defines a descriptor type whose instance values are required to lie within specified upper and lower limits.
 *
 * @param <T> type of the property value
 * @author Brian Remedios
 */
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
