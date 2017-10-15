/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.builders;

import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public abstract class AbstractPropertyBuilder<E, T extends AbstractPropertyBuilder<E, T>> {

    protected String name;
    protected String description;
    protected float uiOrder = 0f;


    protected AbstractPropertyBuilder(String name) {
        this.name = name;
    }


    public T description(String desc) {
        this.description = description;
        return (T) this;
    }


    public T uiOrder(float f) {
        this.uiOrder = f;
        return (T) this;
    }


    public abstract PropertyDescriptor<E> build();


}
