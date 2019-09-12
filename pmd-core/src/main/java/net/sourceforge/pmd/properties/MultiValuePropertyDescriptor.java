/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;


/**
 * Specializes property descriptors for multi valued descriptors.
 * @author Clément Fournier
 * @since 6.0.0
 *
 * @deprecated The hard divide between multi- and single-value properties will be removed with 7.0.0
 */
@Deprecated
public interface MultiValuePropertyDescriptor<V> extends PropertyDescriptor<List<V>> {

    /** Default delimiter for multi-valued properties other than numeric ones. */
    @Deprecated
    char DEFAULT_DELIMITER = '|';

    /** Default delimiter for numeric multi-valued properties. */
    @Deprecated
    char DEFAULT_NUMERIC_DELIMITER = ',';


    /**
     * Return the character being used to delimit multiple property values within a single string. You must ensure that
     * this character does not appear within any rule property values to avoid deserialization errors.
     *
     * @return char
     */
    @Deprecated
    char multiValueDelimiter();


}
