/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.List;

/**
 * @author Clément Fournier
 */
public interface MultiValuePropertyDescriptor<V> extends PropertyDescriptor<List<V>> {

    /**
     * Return the character being used to delimit multiple property values
     * within a single string. You must ensure that this character does not
     * appear within any rule property values to avoid deserialization errors.
     *
     * @return char
     */
    char multiValueDelimiter();


    @Override
    Class<V> type();
}
