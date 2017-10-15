/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.builders;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public abstract class AbstractMultiNumericPropertyBuilder<V, T extends AbstractMultiNumericPropertyBuilder<V, T>>
        extends AbstractMultiValuePropertyBuilder<V, T> {


    protected V lowerLimit;
    protected V upperLimit;


    protected AbstractMultiNumericPropertyBuilder(String name) {
        super(name);
    }


    public T min(V val) {
        this.lowerLimit = val;
        return (T) this;
    }


    public T max(V val) {
        this.upperLimit = val;
        return (T) this;
    }

}
