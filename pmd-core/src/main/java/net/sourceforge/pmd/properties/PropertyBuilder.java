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

import net.sourceforge.pmd.properties.PropertyBuilder.GenericCollectionPropertyBuilder.Supplier;
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
 * @since 6.10.0
 */
public abstract class PropertyBuilder<B extends PropertyBuilder<B, T>, T> {

    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][\\w-]*");
    private final Set<PropertyConstraint<? super T>> validators = new LinkedHashSet<>();
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


    // will maybe be scrapped
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


    @Deprecated
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
     *
     * @deprecated see {@link PropertyDescriptor#uiOrder()}
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public B uiOrder(float f) {
        this.uiOrder = f;
        return (B) this;
    }


    /**
     * Add a constraint on the values that this property may take.
     * The validity of values will be checked when parsing the XML,
     * and invalid values will be reported. A rule will never be run
     * if some of its properties violate some constraints.
     *
     * @param constraint The constraint
     *
     * @return The same builder
     */
    // TODO we could probably specify the order of execution of constraints come 7.0.0, for now this remains unspecified
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
    // Note: we may keep some specialized property builders around to allow for some sugar,
    // e.g. specifying the default value of a regex property as a string, or like the collection one,
    // with varargs for collection types
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
         *
         * @throws IllegalStateException if the default value has already been set
         */
        /* package private */ GenericCollectionPropertyBuilder<T, List<T>> toList() {

            // TODO 7.0.0 this is obviously a lambda
            Supplier<List<T>> listSupplier = new Supplier<List<T>>() {
                @Override
                public List<T> get() {
                    return new ArrayList<>();
                }
            };

            return toCollection(listSupplier);
        }


        private <C extends Collection<T>> GenericCollectionPropertyBuilder<T, C> toCollection(Supplier<C> emptyCollSupplier) {
            if (getDefaultValue() != null) {
                throw new IllegalStateException("The default value is already set!");
            }

            GenericCollectionPropertyBuilder<T, C> result = new GenericCollectionPropertyBuilder<>(getName(),
                                                                                                   getParser(),
                                                                                                   emptyCollSupplier,
                                                                                                   getType());

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
     * Generic builder for a collection-valued property.
     * This class adds overloads to {@linkplain #defaultValues(Object[])}
     * to make its use more flexible.
     *
     * <p>Note: this is designed to support arbitrary collections.
     * Pre-7.0.0, the only collections available from the {@link PropertyFactory}
     * are list types though.
     *
     * @param <V> Component type of the collection
     * @param <C> Collection type for the property being built
     *
     * @author Clément Fournier
     * @since 6.7.0
     */
    public static final class GenericCollectionPropertyBuilder<V, C extends Collection<V>> extends PropertyBuilder<GenericCollectionPropertyBuilder<V, C>, C> {
        private final ValueParser<V> parser;
        private final Supplier<C> emptyCollSupplier;
        private final Class<V> type;
        private char multiValueDelimiter;


        /**
         * Builds a new builder for a collection type. Package-private.
         */
        GenericCollectionPropertyBuilder(String name,
                                         ValueParser<V> parser,
                                         Supplier<C> emptyCollSupplier,
                                         Class<V> type) {
            super(name);
            this.parser = parser;
            this.emptyCollSupplier = emptyCollSupplier;
            this.type = type;
        }


        private C getDefaultValue(Collection<? extends V> list) {
            C coll = emptyCollSupplier.get();
            coll.addAll(list);
            return coll;
        }


        /**
         * Specify a default value.
         *
         * @param val List of values
         *
         * @return The same builder
         */
        @SuppressWarnings("unchecked")
        public GenericCollectionPropertyBuilder<V, C> defaultValue(Collection<? extends V> val) {
            super.defaultValue(getDefaultValue(val));
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
        public GenericCollectionPropertyBuilder<V, C> defaultValues(V... val) {
            super.defaultValue(getDefaultValue(Arrays.asList(val)));
            return this;
        }


        /**
         * Specify a delimiter character. By default it's {@link MultiValuePropertyDescriptor#DEFAULT_DELIMITER}, or {@link
         * MultiValuePropertyDescriptor#DEFAULT_NUMERIC_DELIMITER} for numeric properties.
         *
         * @param delim Delimiter
         *
         * @return The same builder
         *
         * @deprecated PMD 7.0.0 will introduce a new XML syntax for multi-valued properties which will not rely on delimiters.
         * This method is kept until this is implemented for compatibility reasons with the pre-7.0.0 framework, but
         * it will be scrapped come 7.0.0.
         */
        @Deprecated
        public GenericCollectionPropertyBuilder<V, C> delim(char delim) {
            this.multiValueDelimiter = delim;
            return this;
        }


        /**
         * Builds a new property descriptor with the configuration held in this builder.
         *
         * @return A new property
         */
        @SuppressWarnings("unchecked")
        @Override
        public PropertyDescriptor<C> build() {
            // Note: the unchecked cast is safe because pre-7.0.0,
            // we only allow building property descriptors for lists.
            // C is thus always List<V>, and the cast doesn't fail

            // Post-7.0.0, the multi-value property classes will be removed
            // and C will be the actual type parameter of the returned property
            // descriptor

            return (PropertyDescriptor<C>) new GenericMultiValuePropertyDescriptor<>(
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


        // Until we have Java 8
        @Deprecated
        interface Supplier<T> {
            T get();
        }
    }
}
