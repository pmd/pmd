/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static java.util.Collections.emptyList;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.IteratorUtil;
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
    private final String name;
    private String description;
    private T defaultValue;
    protected @Nullable PropertyTypeId typeId;
    protected boolean isXPathAvailable = false;


    PropertyBuilder(String name) {

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name must be provided");
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid name '" + name + "'");
        }
        this.name = name;
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

    /**
     * If true, the property will be made available to XPath queries as
     * an XPath variable. The default is false (except for properties
     * of XPath rules that were defined in XML).
     *
     * @param b Whether to enable or not
     *
     * @return This builder
     */
    public B availableInXPath(boolean b) {
        this.isXPathAvailable = b;
        return (B) this;
    }

    /**
     * Add a constraint on the values that this property may take.
     * The validity of values will be checked when parsing the XML,
     * and invalid values will be reported. A rule will never be run
     * if some of its properties violate some constraints.
     *
     * <p>Constraints should be independent from each other, and should
     * perform no side effects. PMD doesn't specify how many times a
     * constraint predicate will be executed, or in what order.
     *
     * @param constraint The constraint
     *
     * @return The same builder
     *
     * @see net.sourceforge.pmd.properties.constraints.NumericConstraints
     */
    @SuppressWarnings("unchecked")
    public abstract B require(PropertyConstraint<? super T> constraint);


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
     * @throws IllegalArgumentException if the description or default value were not provided
     * @throws IllegalArgumentException if the default value does not satisfy the given constraints
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

        private XmlMapper<T> parser;


        // Class is not final but a package-private constructor restricts inheritance
        BaseSinglePropertyBuilder(String name, XmlMapper<T> parser) {
            super(name);
            this.parser = parser;
        }


        protected XmlMapper<T> getParser() {
            return parser;
        }

        @SuppressWarnings("unchecked")
        @Override
        public B require(PropertyConstraint<? super T> constraint) {
            parser = parser.withConstraint(constraint);
            return (B) this;
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
         *
         * @see #map(Collector)
         */
        public GenericCollectionPropertyBuilder<T, List<T>> toList() {
            return map(Collectors.toList());
        }

        /**
         * Returns a new builder that can be used to build a property
         * with value type {@code <C>}. The validators already added are
         * converted to collection validators. The default value cannot
         * have previously been set. The returned builder will support
         * conversion to and from a delimited string if this property does.
         * Otherwise it will only support the {@code <seq>} syntax.
         *
         * <p>Example usage:
         * <pre>{@code
         *
         * // this can be set both with
         * // <value>a,b,c</value>
         * // and
         * // <seq>
         * //  <value>a</value>
         * //  <value>b</value>
         * // </seq>
         * PropertyDescriptor<Set<String>> whitelistSet =
         *      PropertyFactory.stringProperty("whitelist")
         *                     .desc(...)
         *                     .to(Collectors.toSet())
         *                     .emptyDefaultValue()
         *                     .build();
         *
         * @return A new list property builder
         *
         * @throws IllegalStateException if the default value has already been set
         */
        public <C extends Iterable<T>> GenericCollectionPropertyBuilder<T, C> map(Collector<? super T, ?, ? extends C> collector) {

            if (isDefaultValueSet()) {
                throw new IllegalStateException("The default value is already set!");
            }

            GenericCollectionPropertyBuilder<T, C> result = new GenericCollectionPropertyBuilder<>(getName(), getParser(), collector);

            if (isDescriptionSet()) {
                result.desc(getDescription());
            }

            return result;
        }

        /**
         * Returns a new builder that can be used to build a property
         * handling {@code Optional<T>}. The validators already added
         * are used on the validator property. If the default value was
         * previously set, it is converted to an optional with {@link Optional#ofNullable(Object)}.
         *
         * @return A new property builder for an optional.
         */
        public GenericPropertyBuilder<Optional<T>> toOptional() {
            GenericPropertyBuilder<Optional<T>> result = new GenericPropertyBuilder<>(this.getName(), XmlSyntaxUtils.toOptional(getParser()));

            if (isDefaultValueSet()) {
                result.defaultValue(Optional.ofNullable(getDefaultValue()));
            }

            if (isDescriptionSet()) {
                result.desc(getDescription());
            }

            return result;
        }


        @Override
        public PropertyDescriptor<T> build() {
            return new PropertyDescriptor<>(
                getName(),
                getDescription(),
                getDefaultValue(),
                parser,
                typeId,
                isXPathAvailable);
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
     * This class adds methods related to {@link #defaultValue(Iterable)}
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
    public static final class GenericCollectionPropertyBuilder<V, C extends Iterable<V>> extends PropertyBuilder<GenericCollectionPropertyBuilder<V, C>, C> {

        private XmlMapper<V> itemParser;
        private final Collector<? super V, ?, ? extends C> collector;
        private final List<PropertyConstraint<? super C>> collectionConstraints = new ArrayList<>();


        /**
         * Builds a new builder for a collection type. Package-private.
         */
        GenericCollectionPropertyBuilder(String name,
                                         XmlMapper<V> itemParser,
                                         Collector<? super V, ?, ? extends C> collector) {
            super(name);
            this.itemParser = itemParser;
            this.collector = collector;
        }


        private C getDefaultValue(Iterable<? extends V> list) {
            return IteratorUtil.stream(list).collect(collector);
        }

        @Override
        public GenericCollectionPropertyBuilder<V, C> require(PropertyConstraint<? super C> constraint) {
            collectionConstraints.add(constraint);
            return this;
        }

        /**
         * Specify a default value. This will be converted to type
         * {@code <C>} with the supplied collector.
         *
         * @param val List of values
         *
         * @return The same builder
         */
        public GenericCollectionPropertyBuilder<V, C> defaultValue(Iterable<? extends V> val) {
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
            return this.defaultValue(listOf(head, tail));
        }


        /**
         * Specify that the default value is an empty collection.
         *
         * @return The same builder
         */
        public GenericCollectionPropertyBuilder<V, C> emptyDefaultValue() {
            return this.defaultValue(emptyList());
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
            this.itemParser = itemParser.withConstraint(constraint);
            return this;
        }


        @Override
        public PropertyDescriptor<C> build() {
            XmlMapper<C> syntax = itemParser.supportsStringMapping()
                                  ? XmlSyntaxUtils.seqAndDelimited(itemParser, collector, false, PropertyFactory.DEFAULT_DELIMITER)
                                  : XmlSyntaxUtils.onlySeq(itemParser, collector);

            syntax = XmlSyntaxUtils.withAllConstraints(syntax, collectionConstraints);

            return new PropertyDescriptor<>(
                getName(),
                getDescription(),
                getDefaultValue(),
                syntax,
                typeId,
                isXPathAvailable);
        }
    }
}
