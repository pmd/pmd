/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.builders;

import java.util.List;

import net.sourceforge.pmd.properties.MultiValuePropertyDescriptor;


/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public abstract class AbstractMultiValuePropertyBuilder<V, T extends AbstractMultiValuePropertyBuilder<V, T>>
        extends AbstractPropertyBuilder<List<V>, T> {

    protected List<V> defaultValues;
    protected char multiValueDelimiter = MultiValuePropertyDescriptor.DEFAULT_DELIMITER;


    protected AbstractMultiValuePropertyBuilder(String name) {
        super(name);
    }


    public T deft(List<V> val) {
        this.defaultValues = val;
        return (T) this;
    }


    public T delim(char delim) {
        this.multiValueDelimiter = delim;
        return (T) this;
    }

}
