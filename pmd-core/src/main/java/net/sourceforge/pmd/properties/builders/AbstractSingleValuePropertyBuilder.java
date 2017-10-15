/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.builders;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public abstract class AbstractSingleValuePropertyBuilder<E, T extends AbstractSingleValuePropertyBuilder<E, T>>
        extends AbstractPropertyBuilder<E, T> {

    protected E defaultValue;


    protected AbstractSingleValuePropertyBuilder(String name) {
        super(name);
    }


    public T deft(E val) {
        this.defaultValue = val;
        return (T) this;
    }
}
