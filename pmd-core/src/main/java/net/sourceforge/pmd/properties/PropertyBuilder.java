/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.properties.constraints.PropertyConstraint;
import net.sourceforge.pmd.properties.xml.XmlMapper;
import net.sourceforge.pmd.properties.xml.XmlSyntaxUtils;

// @formatter:off
/**
 * Base class for generic property builders.
 * Property builders are obtained from the {@link PropertyFactory},
 * and are used to build {@link PropertyDescriptor}s.
 *
 * <p>All properties <i>must</i> specify the following attributes to build
 * properly:
 * <ul>
 *   <li>A name: filled-in when obtaining the builder
 *   <li>A description: see {@link #desc(String)}
 *   <li>A default value: see {@link #defaultValue(Object)}
 * </ul>
 *
 * <p>The {@link PropertyDescriptor} may be built after those required steps by
 * calling {@link #build()}.
 *
 * <p>A property builder may throw {@link IllegalArgumentException} at any
 * stage during the build process to indicate invalid input. It usually tries
 * to do so as early as possible, rather than waiting for the call to {@link #build()}.
 *
 * @param <B> Concrete type of this builder instance
 * @param <T> Type of values the property handles
 *
 * @author Clément Fournier
 * @since 6.10.0
 */
// @formatter:on
public abstract class PropertyBuilder<B extends PropertyBuilder<B, T>, T> {

    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][\\w-]*");
    private final Set<PropertyConstraint<? super T>> validators = new LinkedHashSet<>();
    private String name;
    private String description;
    private T defaultValue;
    protected PropertyTypeId typeId;


    PropertyBuilder(String name) {

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name must be provided");
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid name '" + name + "'");
        }
        this.name = name;
    }


    Set<PropertyConstraint<? super T>> getConstraints() {
        return validators;
    }


    String getDescription() {
        if (!isDescriptionSet()) {
            throw new IllegalArgumentException("Description must be provided");
        }
        return description;
    }

    boolean isDescriptionSet() {
        return StringUtils.isNotBlank(description);
    }


    /** Returns the value, asserting it has been set. */
    T getDefaultValue() {
        if (!isDefaultValueSet()) {
            throw new IllegalArgumentException("A default value must be provided");
        }
        return defaultValue;
    }


    boolean isDefaultValueSet() {
        return defaultValue != null;
    }

    /**
     * Specify the description of the property. This is used for documentation.
     * Please describe precisely how the property may change the behaviour of the
     * rule. Providing complete information should be preferred over being concise.
     *
     * <p>Calling this method is required for {@link #build()} to succeed.
     *
     * @param desc The description
     *
     * @return The same builder
     *
     * @throws IllegalArgumentException If the description is null or whitespace
     */
    @SuppressWarnings("unchecked")
    public B desc(String desc) {
        if (StringUtils.isBlank(desc)) {
            throw new IllegalArgumentException("Description must be provided");
        }
        this.description = desc;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    B typeId(PropertyTypeId typeId) {
        this.typeId = typeId;
        return (B) this;
    }

    // TODO 7.0.0 document the following:
    //
    //     * <p>Constraints should be independent from each other, and should
    //     * perform no side effects. PMD doesn't specify how many times a
    //     * constraint predicate will be executed, or in what order.
    //
    // This is superfluous right now bc users may not create their own constraints


    /**
     * Add a constraint on the values that this property may take.
     * The validity of values will be checked when parsing the XML,
     * and invalid values will be reported. A rule will never be run
     * if some of its properties violate some constraints.
     *
     * @param constraint The constraint
     *
     * @return The same builder
     *
     * @see net.sourceforge.pmd.properties.constraints.NumericConstraints
     */
    @SuppressWarnings("unchecked")
    public B require(PropertyConstraint<? super T> constraint) {
        validators.add(constraint);
        return (B) this;
    }


    /**
     * Specify a default value. Some subclasses provide convenient
     * related methods, see e.g. {@link GenericCollectionPropertyBuilder#defaultValues(Object, Object[])}.
     * Using the null value is prohibited.
     *
     * <p>Calling this method is required for {@link #build()} to succeed.
     *
     * @param val Default value
     *
     * @return The same builder
     *
     * @throws IllegalArgumentException If the argument is null
     */
    @SuppressWarnings("unchecked")
    public B defaultValue(@NonNull T val) {
        //noinspection ConstantConditions
        if (val == null) {
            throw new IllegalArgumentException("Property values may not be null.");
        }
        this.defaultValue = val;
        return (B) this;
    }


    /**
     * Builds the descriptor and returns it.
     *
     * @return The built descriptor
     *
     * @throws IllegalArgumentException if the description or default value were not provided, or if the default value doesn't satisfy the given constraints
     */
    public abstract PropertyDescriptor<T> build();


    /**
     * Returns the name of the property to be built.
     */
    public String getName() {
        return name;
    }


    // Technically this may very well be merged into PropertyBuilder
    // We'd have all properties (even collection properties) enjoy a ValueParser,
    // which means they could be parsed in a <value> tag (collections would use delimiters) if they opt in.
    // The delimiters wouldn't be configurable (we'd take our current defaults). If they could ambiguous
    // then the <seq> syntax should be the only one available.
    // This would allow specifying eg lists of numbers as <value>1,2,3</value>, for which the <seq> syntax would look clumsy
    abstract static class BaseSinglePropertyBuilder<B extends PropertyBuilder<B, T>, T> extends PropertyBuilder<B, T> {

        private final XmlMapper<T> parser;


        // Class is not final but a package-private constructor restricts inheritance
        BaseSinglePropertyBuilder(String name, XmlMapper<T> parser) {
            super(name);
            this.parser = parser;
        }


        protected XmlMapper<T> getParser() {
            return parser;
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

            if (isDefaultValueSet()) {
                throw new IllegalStateException("The default value is already set!");
            }

            GenericCollectionPropertyBuilder<T, List<T>> result =
                new GenericCollectionPropertyBuilder<>(getName(), getParser(), ArrayList::new);

            if (isDescriptionSet()) {
                result.desc(getDescription());
            }

            for (PropertyConstraint<? super T> validator : getConstraints()) {
                result.require(validator.toCollectionConstraint());
            }

            return result;

        }

        /**
         * Returns a new builder that can be used to build a property
         * handling {@code Optional<T>}. The validators already added
         * are used on the validator property. If the default value was
         * previously set, it is converted to an optional with {@link Optional#ofNullable(Object)}.
         *
         * @return A new list property builder
         *
         * @throws IllegalStateException if the default value has already been set
         */
        public GenericPropertyBuilder<Optional<T>> toOptional() {
            return new GenericPropertyBuilder<Optional<T>>(this.getName(), XmlSyntaxUtils.toOptional(getParser())) {
                {
                    BaseSinglePropertyBuilder<B, T> base = BaseSinglePropertyBuilder.this;
                    if (base.isDefaultValueSet()) {
                        this.defaultValue(Optional.ofNullable(base.getDefaultValue()));
                    }

                    if (base.isDescriptionSet()) {
                        this.desc(base.getDescription());
                    }

                    for (PropertyConstraint<? super T> validator : base.getConstraints()) {
                        this.require(validator.toOptionalConstraint());
                    }
                }
            };
        }


        @Override
        public PropertyDescriptor<T> build() {
            return new GenericPropertyDescriptor<>(
                getName(),
                getDescription(),
                getDefaultValue(),
                getConstraints(),
                parser,
                typeId
            );
        }
    }

    /**
     * Generic builder for a single-value property.
     *
     * @param <T> Type of values the property handles
     *
     * @author Clément Fournier
     * @since 6.10.0
     */
    // Note: This type is used to fix the first type parameter for classes that don't need more API.
    public static class GenericPropertyBuilder<T> extends BaseSinglePropertyBuilder<GenericPropertyBuilder<T>, T> {

        GenericPropertyBuilder(String name, XmlMapper<T> parser) {
            super(name, parser);
        }
    }


    /**
     * Specialized builder for regex properties. Allows specifying the pattern as a
     * string, with {@link #defaultValue(String)}.
     *
     * @author Clément Fournier
     * @since 6.10.0
     */
    public static final class RegexPropertyBuilder extends BaseSinglePropertyBuilder<RegexPropertyBuilder, Pattern> {

        RegexPropertyBuilder(String name) {
            super(name, XmlSyntaxUtils.REGEX);
        }


        /**
         * Specify a default value using a string pattern. The argument is
         * compiled to a pattern using {@link Pattern#compile(String)}.
         *
         * @param pattern String pattern
         *
         * @return The same builder
         *
         * @throws java.util.regex.PatternSyntaxException If the argument is not a valid pattern
         */
        public RegexPropertyBuilder defaultValue(String pattern) {
            super.defaultValue(Pattern.compile(pattern));
            return this;
        }


        /**
         * Specify a default value using a string pattern. The argument is
         * compiled to a pattern using {@link Pattern#compile(String, int)}.
         *
         * @param pattern String pattern
         * @param flags   Regex compilation flags, the same as for {@link Pattern#compile(String, int)}
         *
         * @return The same builder
         *
         * @throws java.util.regex.PatternSyntaxException If the argument is not a valid pattern
         * @throws IllegalArgumentException               If bit values other than those corresponding to the defined
         *                                                match flags are set in {@code flags}
         */
        public RegexPropertyBuilder defaultValue(String pattern, int flags) {
            super.defaultValue(Pattern.compile(pattern, flags));
            return this;
        }
    }


    /**
     * Generic builder for a collection-valued property.
     * This class adds methods related to {@link #defaultValue(Collection)}
     * to make its use more flexible. See e.g. {@link #defaultValues(Object, Object[])}.
     *
     * <p>Note: this is designed to support arbitrary collections.
     * Pre-7.0.0, the only collections available from the {@link PropertyFactory}
     * are list types though.
     *
     * @param <V> Component type of the collection
     * @param <C> Collection type for the property being built
     *
     * @author Clément Fournier
     * @since 6.10.0
     */
    public static final class GenericCollectionPropertyBuilder<V, C extends Collection<V>> extends PropertyBuilder<GenericCollectionPropertyBuilder<V, C>, C> {

        private final XmlMapper<V> parser;
        private final Supplier<C> emptyCollSupplier;
        private char multiValueDelimiter = PropertyFactory.DEFAULT_DELIMITER;


        /**
         * Builds a new builder for a collection type. Package-private.
         */
        GenericCollectionPropertyBuilder(String name,
                                         XmlMapper<V> parser,
                                         Supplier<C> emptyCollSupplier) {
            super(name);
            this.parser = parser;
            this.emptyCollSupplier = emptyCollSupplier;
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
         * Specify default values. To specify an empty
         * default value, use {@link #emptyDefaultValue()}.
         *
         * @param head First value
         * @param tail Rest of the values
         *
         * @return The same builder
         */
        @SuppressWarnings("unchecked")
        public GenericCollectionPropertyBuilder<V, C> defaultValues(V head, V... tail) {
            List<V> tmp = new ArrayList<>(tail.length + 1);
            tmp.add(head);
            tmp.addAll(Arrays.asList(tail));
            return super.defaultValue(getDefaultValue(tmp));
        }


        /**
         * Specify that the default value is an empty collection.
         *
         * @return The same builder
         */
        public GenericCollectionPropertyBuilder<V, C> emptyDefaultValue() {
            return super.defaultValue(getDefaultValue(Collections.<V>emptyList()));
        }


        /**
         * Require that the given constraint be fulfilled on each item of the
         * value of this property. This is a convenient shorthand for
         * {@code require(constraint.toCollectionConstraint())}.
         *
         * @param constraint Constraint to impose on the items of the collection value
         *
         * @return The same builder
         */
        public GenericCollectionPropertyBuilder<V, C> requireEach(PropertyConstraint<? super V> constraint) {
            return super.require(constraint.toCollectionConstraint());
        }


        /**
         * Specify a delimiter character. By default it's {@value PropertyFactory#DEFAULT_DELIMITER},
         * or {@value PropertyFactory#DEFAULT_NUMERIC_DELIMITER} for numeric properties.
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


        @SuppressWarnings("unchecked")
        @Override
        public PropertyDescriptor<C> build() {
            // Note: the unchecked cast is safe because pre-7.0.0,
            // we only allow building property descriptors for lists.
            // C is thus always List<V>, and the cast doesn't fail

            // Post-7.0.0, the multi-value property classes will be removed
            // and C will be the actual type parameter of the returned property
            // descriptor

            XmlMapper<C> syntax = parser.supportsStringMapping()
                                  ? XmlSyntaxUtils.seqAndDelimited(parser, emptyCollSupplier, false, multiValueDelimiter)
                                  : XmlSyntaxUtils.onlySeq(parser, emptyCollSupplier);

            return new GenericPropertyDescriptor<>(
                getName(),
                getDescription(),
                getDefaultValue(),
                getConstraints(),
                syntax,
                typeId
            );
        }
    }
}
