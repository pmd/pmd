/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


/**
 * Base class for generic property builders.
 *
 * @param <B> Concrete type of this builder instance
 * @param <T> Type of values the property handles
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
public abstract class AbstractPropertyBuilder<B extends AbstractPropertyBuilder<B, T>, T> {
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][\\w-]*");
    private final Set<PropertyValidator<T>> validators = new LinkedHashSet<>();
    private String name;
    private String description;
    private float uiOrder = 0f;
    private T defaultValue;


    AbstractPropertyBuilder(String name) {

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name must be provided");
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid name '" + name + "'");
        }
        this.name = name;
    }


    Set<PropertyValidator<T>> getValidators() {
        return validators;
    }


    String getDescription() {
        return description;
    }


    float getUiOrder() {
        return uiOrder;
    }


    T getDefaultValue() {
        return defaultValue;
    }


    /**
     * Specify the description of the property.
     *
     * @param desc The description
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public B desc(String desc) {
        if (StringUtils.isBlank(desc)) {
            throw new IllegalArgumentException("Description must be provided");
        }
        this.description = desc;
        return (B) this;
    }


    /**
     * Specify the UI order of the property.
     *
     * @param f The UI order
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public B uiOrder(float f) {
        this.uiOrder = f;
        return (B) this;
    }


    @SuppressWarnings("unchecked")
    B addValidator(PropertyValidator<T> validator) {
        validators.add(validator);
        return (B) this;
    }


    /**
     * Specify a default value.
     *
     * @param val Value
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public B defaultValue(T val) {
        this.defaultValue = val;
        return (B) this;
    }


    /**
     * Builds the descriptor and returns it.
     *
     * @return The built descriptor
     *
     * @throws IllegalArgumentException if parameters are incorrect
     */
    public abstract PropertyDescriptor<T> build();


    /**
     * Returns the name of the property to be built.
     */
    public String getName() {
        return name;
    }


    /**
     * Builder for a generic single-value property.
     *
     * <p>This class is abstract because the B type parameter
     * prevents it to be instantiated anyway. That type parameter
     * is of use to more refined concrete subclasses.
     *
     * @param <B> Concrete type of this builder instance
     * @param <T> Type of values the property handles
     *
     * @author Clément Fournier
     * @since 6.7.0
     */
    public abstract static class GenericPropertyBuilder<B extends GenericPropertyBuilder<B, T>, T> extends AbstractPropertyBuilder<B, T> {


        private final ValueParser<T> parser;
        private final Class<T> type;


        GenericPropertyBuilder(String name, ValueParser<T> parser, Class<T> type) {
            super(name);
            this.parser = parser;
            this.type = type;
        }


        @Override
        public PropertyDescriptor<T> build() {
            return new GenericPropertyDescriptor<>(
                    getName(),
                    getDescription(),
                    getUiOrder(),
                    getDefaultValue(),
                    getValidators(),
                    parser,
                    type
            );
        }


    }

    /**
     * Builder for a generic multi-value property.
     *
     * <p>This class is abstract because the B type parameter
     * prevents it to be instantiated anyway. That type parameter
     * is of use to more refined concrete subclasses.
     *
     * @param <B> Concrete type of this builder instance
     * @param <V> Type of values the property handles. This is the component type of the list
     *
     * @author Clément Fournier
     * @since 6.7.0
     */
    public abstract static class AbstractGenericMultiPropertyBuilder<B extends AbstractPropertyBuilder<B, List<V>>, V> extends AbstractPropertyBuilder<B, List<V>> {
        private final Set<PropertyValidator<V>> componentValidators = new LinkedHashSet<>();
        private final ValueParser<V> parser;
        private final Class<V> type;


        AbstractGenericMultiPropertyBuilder(String name, ValueParser<V> parser, Class<V> type) {
            super(name);
            this.parser = parser;
            this.type = type;
        }


        /**
         * Specify a default value.
         *
         * @param val List of values
         *
         * @return The same builder
         */
        @SuppressWarnings("unchecked")
        public B defaultValues(Collection<? extends V> val) {
            super.defaultValue(new ArrayList<>(val));
            return (B) this;
        }


        /**
         * Specify default values.
         *
         * @param val List of values
         *
         * @return The same builder
         */
        @SuppressWarnings("unchecked")
        public B defaultValues(V... val) {
            super.defaultValue(Arrays.asList(val));
            return (B) this;
        }


        @Override
        public PropertyDescriptor<List<V>> build() {
            return new GenericMultiValuePropertyDescriptor<V>(
                    getName(),
                    getDescription(),
                    getUiOrder(),
                    getDefaultValue(),
                    getValidators(),
                    componentValidators,
                    parser,
                    type
            );
        }
    }
}
