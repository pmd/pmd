/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

/**
 * Defines a descriptor type whose instance values are required lie within specified upper and 
 * lower limits.
 *  
 * @author Brian Remedios
 *
 * @param <T>
 */
public interface NumericPropertyDescriptor<T extends Object > extends PropertyDescriptor<T> {

    /**
     *  Returns the maximum value that instances of the property can have
     * @return Number
     */
    Number upperLimit();
    
    /**
     *  Returns the minimum value that instances of the property can have
     * @return Number
     */
    Number lowerLimit();
}
