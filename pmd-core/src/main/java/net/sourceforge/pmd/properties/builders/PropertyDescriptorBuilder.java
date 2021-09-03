/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.builders;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.properties.PropertyBuilder;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * Base class for property builders.
 *
 * @param <E> Value type of the built descriptor
 * @param <T> Concrete type of this builder instance. Removes code duplication at the expense of a few unchecked casts.
 *            Everything goes well if this parameter's value is correctly set.
 *
 * @deprecated From 7.0.0 on, the only supported way to build properties will be through {@link PropertyFactory}.
 *             This class hierarchy is replaced by the newer {@link PropertyBuilder}.
 * @author Clément Fournier
 * @since 6.0.0
 */
@Deprecated
public abstract class PropertyDescriptorBuilder<E, T extends PropertyDescriptorBuilder<E, T>> {

    protected String name;
    protected String description;
    protected float uiOrder = 0f;
    protected boolean isDefinedInXML = false;


    protected PropertyDescriptorBuilder(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name must be provided");
        }
        this.name = name;
    }


    /**
     * Specify the description of the property.
     *
     * @param desc The description
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public T desc(String desc) {
        if (StringUtils.isBlank(desc)) {
            throw new IllegalArgumentException("Description must be provided");
        }
        this.description = desc;
        return (T) this;
    }


    /**
     * Specify the UI order of the property.
     *
     * @param f The UI order
     *
     * @return The same builder
     * @deprecated See {@link PropertyDescriptor#uiOrder()}
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public T uiOrder(float f) {
        this.uiOrder = f;
        return (T) this;
    }


    /**
     * Builds the descriptor and returns it.
     *
     * @return The built descriptor
     * @throws IllegalArgumentException if parameters are incorrect
     */
    public abstract PropertyDescriptor<E> build();


    /**
     * Returns the name of the property to be built.
     */
    public String getName() {
        return name;
    }

}
