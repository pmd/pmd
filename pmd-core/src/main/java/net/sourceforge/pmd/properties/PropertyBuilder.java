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

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilder;
import net.sourceforge.pmd.properties.constraints.PropertyConstraint;


/**
 * Base class for generic property builders.
 * Note: from 7.0.0 on, all property builders will
 * extend this class instead of {@link PropertyDescriptorBuilder}.
 *
 * @param <B> Concrete type of this builder instance
 * @param <T> Type of values the property handles
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
@Experimental
public abstract class PropertyBuilder<B extends PropertyBuilder<B, T>, T> {
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][\\w-]*");

    boolean isDefinedExternally;
    private final Set<PropertyConstraint<? super T>> validators = new LinkedHashSet<>();
    private String name;
    private String description;
    private float uiOrder = 0f;
    private T defaultValue;


    PropertyBuilder(String name) {

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name must be provided");
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid name '" + name + "'");
        }
        this.name = name;
    }


    @Deprecated
    void setDefinedExternally(boolean bool) {
        this.isDefinedExternally = bool;
    }


    Set<PropertyConstraint<? super T>> getConstraints() {
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


    /**
     * Add a constraint on the values that this property may take.
     * The validity of values will be checked when constructing the XML
     * and invalid values will be reported.
     *
     * @param constraint The constraint
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public B require(PropertyConstraint<? super T> constraint) {
        validators.add(constraint);
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
     * Generic builder for a single-value property.
     *
     * @param <T> Type of values the property handles
     *
     * @author Clément Fournier
     * @since 6.7.0
     */
    @Experimental
    public static final class GenericPropertyBuilder<T> extends PropertyBuilder<GenericPropertyBuilder<T>, T> {


        private final ValueParser<T> parser;
        private final Class<T> type;


        GenericPropertyBuilder(String name, ValueParser<T> parser, Class<T> type) {
            super(name);
            this.parser = parser;
            this.type = type;
        }


        protected ValueParser<T> getParser() {
            return parser;
        }


        protected Class<T> getType() {
            return type;
        }


        /**
         * Returns a new builder that can be used to build a property
         * handling lists of Ts. The validators already added are
         * converted to list validators. The default value cannot have
         * previously been set.
         *
         * @return A new list property builder
         */
        public GenericListPropertyBuilder<T> toList() {
            if (getDefaultValue() != null) {
                throw new IllegalStateException("The default value is already set!");
            }

            GenericListPropertyBuilder<T> result = new GenericListPropertyBuilder<>(getName(), getParser(), getType());

            for (PropertyConstraint<? super T> validator : getConstraints()) {
                result.require(validator.toMulti());
            }

            return result;
        }


        @Override
        public PropertyDescriptor<T> build() {
            return new GenericPropertyDescriptor<>(
                    getName(),
                    getDescription(),
                    getUiOrder(),
                    getDefaultValue(),
                    getConstraints(),
                    parser,
                    isDefinedExternally,
                    type
            );
        }


    }

    /**
     * Generic builder for a multi-value property.
     *
     * @param <V> Component type of the list
     *
     * @author Clément Fournier
     * @since 6.7.0
     */
    @Experimental
    public static final class GenericListPropertyBuilder<V> extends PropertyBuilder<GenericListPropertyBuilder<V>, List<V>> {
        private final ValueParser<V> parser;
        private final Class<V> type;
        private char multiValueDelimiter;


        GenericListPropertyBuilder(String name, ValueParser<V> parser, Class<V> type) {
            super(name);
            this.parser = parser;
            this.type = type;
        }


        protected ValueParser<V> getParser() {
            return parser;
        }


        protected Class<V> getType() {
            return type;
        }


        /**
         * Specify a default value.
         *
         * @param val List of values
         *
         * @return The same builder
         */
        @SuppressWarnings("unchecked")
        public GenericListPropertyBuilder<V> defaultValues(Collection<? extends V> val) {
            super.defaultValue(new ArrayList<>(val));
            return this;
        }


        /**
         * Specify default values.
         *
         * @param val List of values
         *
         * @return The same builder
         */
        @SuppressWarnings("unchecked")
        public GenericListPropertyBuilder<V> defaultValues(V... val) {
            super.defaultValue(Arrays.asList(val));
            return this;
        }


        /**
         * Specify a delimiter character. By default it's {@link MultiValuePropertyDescriptor#DEFAULT_DELIMITER}, or {@link
         * MultiValuePropertyDescriptor#DEFAULT_NUMERIC_DELIMITER} for numeric properties.
         *
         * @param delim Delimiter
         *
         * @return The same builder
         */
        @SuppressWarnings("unchecked")
        public GenericListPropertyBuilder<V> delim(char delim) {
            this.multiValueDelimiter = delim;
            return this;
        }


        @Override
        public PropertyDescriptor<List<V>> build() {
            return new GenericMultiValuePropertyDescriptor<V>(
                    getName(),
                    getDescription(),
                    getUiOrder(),
                    getDefaultValue(),
                    getConstraints(),
                    parser,
                    multiValueDelimiter,
                    type
            );
        }
    }
}
