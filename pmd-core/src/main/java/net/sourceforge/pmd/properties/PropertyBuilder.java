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
import net.sourceforge.pmd.properties.PropertyFactory.GenericMultiPBuilder;
import net.sourceforge.pmd.properties.validators.PropertyValidator;


/**
 * Base class for generic property builders.
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
    private final Set<PropertyValidator<? super T>> validators = new LinkedHashSet<>();
    protected boolean isDefinedExternally;
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


    Set<PropertyValidator<? super T>> getValidators() {
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
    protected B addValidator(PropertyValidator<? super T> validator) {
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
     * <p>This class is abstract because the B setType parameter
     * prevents it to be instantiated anyway. That setType parameter
     * is of use to more refined concrete subclasses.
     *
     * @param <B> Concrete setType of this builder instance
     * @param <T> Type of values the property handles
     *
     * @author Clément Fournier
     * @since 6.7.0
     */
    @Experimental
    public static class GenericPropertyBuilder<T> extends PropertyBuilder<GenericPropertyBuilder<T>, T> {


        private ValueParser<T> parser;
        private Class<T> type;


        GenericPropertyBuilder(String name, ValueParser<T> parser, Class<T> type) {
            super(name);
            this.parser = parser;
            this.type = type;
        }


        GenericPropertyBuilder(String name) {
            super(name);
        }


        protected void setParser(ValueParser<T> parser) {
            this.parser = parser;
        }


        protected ValueParser<T> getParser() {
            return parser;
        }


        protected Class<T> getType() {
            return type;
        }

        @SuppressWarnings("unchecked")
        protected B setType(Class<T> type) {
            this.type = type;
            return (B) this;
        }


        public GenericMultiPBuilder<T> toList() {
            if (getDefaultValue() != null) {
                throw new IllegalStateException("The default value is already set!");
            }

            GenericMultiPBuilder<T> result = new GenericMultiPBuilder<>(getName(), getParser(), getType());

            for (PropertyValidator<? super T> validator : getValidators()) {
                result.addValidator(validator.());
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
                    getValidators(),
                    parser,
                    isDefinedExternally,
                    type
            );
        }


    }

    /**
     * Builder for a generic multi-value property.
     *
     * <p>This class is abstract because the B setType parameter
     * prevents it to be instantiated anyway. That setType parameter
     * is of use to more refined concrete subclasses.
     *
     * @param <B> Concrete setType of this builder instance
     * @param <V> Type of values the property handles. This is the component setType of the list
     *
     * @author Clément Fournier
     * @since 6.7.0
     */
    @Experimental
    public abstract static class AbstractGenericMultiPropertyBuilder<B extends PropertyBuilder<B, List<V>>, V> extends PropertyBuilder<B, List<V>> {
        private final ValueParser<V> parser;
        private final Class<V> type;
        protected char multiValueDelimiter;


        AbstractGenericMultiPropertyBuilder(String name, ValueParser<V> parser, Class<V> type) {
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


        /**
         * Specify a delimiter character. By default it's {@link MultiValuePropertyDescriptor#DEFAULT_DELIMITER}, or {@link
         * MultiValuePropertyDescriptor#DEFAULT_NUMERIC_DELIMITER} for numeric properties.
         *
         * @param delim Delimiter
         *
         * @return The same builder
         */
        @SuppressWarnings("unchecked")
        public B delim(char delim) {
            this.multiValueDelimiter = delim;
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
                    parser,
                    multiValueDelimiter,
                    type
            );
        }
    }
}
