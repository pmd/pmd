/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static java.util.Arrays.asList;
import static net.sourceforge.pmd.properties.internal.PropertyParsingUtil.enumerationParser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.properties.PropertyBuilder.GenericCollectionPropertyBuilder;
import net.sourceforge.pmd.properties.PropertyBuilder.GenericPropertyBuilder;
import net.sourceforge.pmd.properties.PropertyBuilder.RegexPropertyBuilder;
import net.sourceforge.pmd.properties.internal.PropertyParsingUtil;
import net.sourceforge.pmd.util.CollectionUtil;

//@formatter:off
/**
 * Provides factory methods for common property types.
 * Note: from 7.0.0 on, this will be the only way to
 * build property descriptors. Concrete property classes
 * and their constructors/ builders will be gradually
 * deprecated before 7.0.0.
 *
 * <h2>Usage</h2>
 *
 * Properties are a way to make your rule configurable by
 * letting a user fill in some config value in their
 * ruleset XML.
 *
 * As a rule developer, to declare a property on your rule, you
 * must:
 * <ul>
 *     <li>Build a {@link PropertyDescriptor} using one of
 *     the factory methods of this class, and providing the
 *     {@linkplain PropertyBuilder builder} with the required
 *     info.
 *     <li>Define the property on your rule using {@link PropertySource#definePropertyDescriptor(PropertyDescriptor)}.
 *     This should be done in your rule's constructor.
 * </ul>
 *
 * You can then retrieve the value configured by the user in your
 * rule using {@link PropertySource#getProperty(PropertyDescriptor)}.
 *
 * <h2>Example</h2>
 *
 * <pre>
 * class MyRule {
 *   // The property descriptor may be static, it can be shared across threads.
 *   private static final {@link PropertyDescriptor}&lt;Integer&gt; myIntProperty
 *     = PropertyFactory.{@linkplain #intProperty(String) intProperty}("myIntProperty")
 *                      .{@linkplain PropertyBuilder#desc(String) desc}("This is my property")
 *                      .{@linkplain PropertyBuilder#defaultValue(Object) defaultValue}(3)
 *                      .{@linkplain PropertyBuilder#require(PropertyConstraint) require}(inRange(0, 100))   // constraints are checked before the rule is run
 *                      .{@linkplain PropertyBuilder#build() build}();
 *
 *   // ..
 *
 *   public MyRule() {
 *     {@linkplain PropertySource#definePropertyDescriptor(PropertyDescriptor) definePropertyDescriptor}(myIntProperty);
 *   }
 *
 *     // ... somewhere in the rule
 *
 *     int myPropertyValue = {@linkplain PropertySource#getProperty(PropertyDescriptor) getProperty(myIntProperty)};
 *     // use it.
 *
 * }
 * </pre>
 *
 *
 * @author Clément Fournier
 * @since 6.10.0
 */
//@formatter:on
public final class PropertyFactory {


    /**
     * Default delimiter for all properties. Note that in PMD 6 this was
     * the pipe character {@code |}, while now it is {@value}. In PMD 6,
     * numeric properties had a different delimiter, whereas in PMD 7, all
     * properties have the same delimiter.
     */
    public static final char DEFAULT_DELIMITER = ',';


    private PropertyFactory() {

    }


    /**
     * Returns a builder for an integer property. The property descriptor
     * will by default accept any value conforming to the format specified
     * by {@link Integer#parseInt(String)}, e.g. {@code 1234} or {@code -123}.
     *
     * <p>Note that that parser only supports decimal representations.
     *
     * <p>Acceptable values may be further refined by {@linkplain PropertyBuilder#require(PropertyConstraint) adding constraints}.
     * The class {@link NumericConstraints} provides some useful ready-made constraints
     * for that purpose.
     *
     * @param name Name of the property to build
     *
     * @return A new builder
     *
     * @see NumericConstraints
     */
    public static GenericPropertyBuilder<Integer> intProperty(String name) {
        return new GenericPropertyBuilder<>(name, PropertyParsingUtil.INTEGER);
    }


    /**
     * Returns a builder for a property having as value a list of integers. The
     * format of the individual items is the same as for {@linkplain #intProperty(String) intProperty}.
     *
     * @param name Name of the property to build
     *
     * @return A new builder
     */
    public static GenericCollectionPropertyBuilder<Integer, List<Integer>> intListProperty(String name) {
        return intProperty(name).toList();
    }


    /**
     * Returns a builder for a long integer property. The property descriptor
     * will by default accept any value conforming to the format specified
     * by {@link Long#parseLong(String)}, e.g. {@code 1234455678854}.
     *
     * <p>Note that that parser only supports decimal representations, and that neither
     * the character L nor l is permitted to appear at the end of the string as a type
     * indicator, as would be permitted in Java source.
     *
     * <p>Acceptable values may be further refined by {@linkplain PropertyBuilder#require(PropertyConstraint) adding constraints}.
     * The class {@link NumericConstraints} provides some useful ready-made constraints
     * for that purpose.
     *
     * @param name Name of the property to build
     *
     * @return A new builder
     *
     * @see NumericConstraints
     */
    public static GenericPropertyBuilder<Long> longIntProperty(String name) {
        return new GenericPropertyBuilder<>(name, PropertyParsingUtil.LONG);
    }


    /**
     * Returns a builder for a property having as value a list of long integers. The
     * format of the individual items is the same as for {@linkplain #longIntProperty(String)} longIntProperty}.
     *
     * @param name Name of the property to build
     *
     * @return A new builder
     */
    public static GenericCollectionPropertyBuilder<Long, List<Long>> longIntListProperty(String name) {
        return longIntProperty(name).toList();
    }


    /**
     * Returns a builder for a double property. The property descriptor
     * will by default accept any value conforming to the format specified
     * by {@link Double#valueOf(String)}, e.g. {@code 0}, {@code .93}, or {@code 1e-1}.
     * Acceptable values may be further refined by {@linkplain PropertyBuilder#require(PropertyConstraint) adding constraints}.
     * The class {@link NumericConstraints} provides some useful ready-made constraints
     * for that purpose.
     *
     * @param name Name of the property to build
     *
     * @return A new builder
     *
     * @see NumericConstraints
     */
    public static GenericPropertyBuilder<Double> doubleProperty(String name) {
        return new GenericPropertyBuilder<>(name, PropertyParsingUtil.DOUBLE);
    }


    /**
     * Returns a builder for a property having as value a list of decimal numbers. The
     * format of the individual items is the same as for {@linkplain #doubleProperty(String) doubleProperty}.
     *
     * @param name Name of the property to build
     *
     * @return A new builder
     */
    public static GenericCollectionPropertyBuilder<Double, List<Double>> doubleListProperty(String name) {
        return doubleProperty(name).toList();
    }


    /**
     * Returns a builder for a regex property. The value type of such
     * a property is {@link java.util.regex.Pattern}. For this use case, this type of
     * property should be preferred over {@linkplain #stringProperty(String) stringProperty}
     * as pattern compilation, including syntax errors, are handled transparently to
     * the rule.
     *
     * @param name Name of the property to build
     *
     * @return A new builder
     */
    public static RegexPropertyBuilder regexProperty(String name) {
        return new RegexPropertyBuilder(name);
    }


    /**
     * Returns a builder for a string property. The property descriptor
     * will accept any string, and performs no expansion of escape
     * sequences (e.g. {@code \n} in the XML will be represented as the
     * character sequence '\' 'n' and not the line-feed character '\n').
     * The value of a string attribute is trimmed.
     * This behaviour could be changed with PMD 7.0.0.
     *
     * @param name Name of the property to build
     *
     * @return A new builder
     */
    public static GenericPropertyBuilder<String> stringProperty(String name) {
        return new GenericPropertyBuilder<>(name, PropertyParsingUtil.STRING);
    }

    /**
     * Returns a builder for a property having as value a list of strings. The
     * format of the individual items is the same as for {@linkplain #stringProperty(String) stringProperty}.
     *
     * @param name Name of the property to build
     *
     * @return A new builder
     */
    public static GenericCollectionPropertyBuilder<String, List<String>> stringListProperty(String name) {
        return stringProperty(name).toList();
    }


    /**
     * Returns a builder for a character property. The property descriptor
     * will accept any single character string. No unescaping is performed
     * other than what the XML parser does itself. That means that Java
     * escape sequences are not expanded: e.g. "\n", will be represented as the
     * character sequence '\' 'n', so it's not a valid value for this type
     * of property. On the other hand, XML character references are expanded,
     * like {@literal &amp;} ('&amp;') or {@literal &lt;} ('&lt;').
     *
     * @param name Name of the property to build
     *
     * @return A new builder
     */
    public static GenericPropertyBuilder<Character> charProperty(String name) {
        return new GenericPropertyBuilder<>(name, PropertyParsingUtil.CHARACTER);
    }


    /**
     * Returns a builder for a property having as value a list of characters. The
     * format of the individual items is the same as for {@linkplain #charProperty(String) charProperty}.
     *
     * @param name Name of the property to build
     *
     * @return A new builder
     */
    public static GenericCollectionPropertyBuilder<Character, List<Character>> charListProperty(String name) {
        return charProperty(name).toList();
    }


    /**
     * Returns a builder for a boolean property. The boolean is parsed from
     * the XML using {@link Boolean#valueOf(String)}, i.e. the only truthy
     * value is the string "true", and all other string values are falsy.
     *
     * @param name Name of the property to build
     *
     * @return A new builder
     */
    public static GenericPropertyBuilder<Boolean> booleanProperty(String name) {
        return new GenericPropertyBuilder<>(name, PropertyParsingUtil.BOOLEAN);
    }

    /**
     * Returns a builder for an enumerated property. Such a property can be
     * defined for any type {@code <T>}, provided the possible values can be
     * indexed by strings. This is enforced by passing a {@code Map<String, T>}
     * at construction time, which maps labels to values. If {@link Map#get(Object)}
     * returns null for a given label, then the value is rejected. Null values
     * are hence prohibited.
     *
     * @param name        Name of the property to build
     * @param nameToValue Map of labels to values. The null key is ignored.
     * @param <T>         Value type of the property
     *
     * @throws IllegalArgumentException If the map contains a null value or key
     *
     * @return A new builder
     */
    public static <T> GenericPropertyBuilder<T> enumProperty(String name, Map<String, T> nameToValue) {
        PropertySerializer<T> parser = enumerationParser(
            nameToValue,
            t -> Objects.requireNonNull(CollectionUtil.getKeyOfValue(nameToValue, t))
        );
        return new GenericPropertyBuilder<>(name, parser);
    }

    /**
     * Returns a builder for an enumerated property for the given enum
     * class, using the {@link Object#toString() toString} of its enum
     * constants as labels.
     *
     * @param name      Property name
     * @param enumClass Enum class
     * @param <T>       Type of the enum class
     *
     * @return A new builder
     */
    public static <T extends Enum<T>> GenericPropertyBuilder<T> enumProperty(String name, Class<T> enumClass) {
        return enumProperty(name, enumClass, Object::toString);
    }

    /**
     * Returns a builder for an enumerated property for the given enum
     * class, using the given function to label its constants.
     *
     * @param name       Property name
     * @param enumClass  Enum class
     * @param labelMaker Function that labels each constant
     * @param <T>        Type of the enum class
     *
     * @return A new builder
     *
     * @throws NullPointerException     If any of the arguments is null
     * @throws IllegalArgumentException If the label maker returns null on some constant
     * @throws IllegalStateException    If the label maker maps two constants to the same label
     */
    public static <T extends Enum<T>> GenericPropertyBuilder<T> enumProperty(String name,
                                                                             Class<T> enumClass,
                                                                             Function<? super T, @NonNull String> labelMaker) {
        // don't use a merge function, so that it throws if multiple
        // values have the same key
        Map<String, T> labelsToValues = Arrays.stream(enumClass.getEnumConstants())
                                              .collect(Collectors.toMap(labelMaker, t -> t));

        return new GenericPropertyBuilder<>(name, enumerationParser(labelsToValues, labelMaker));
    }


    /**
     * Returns a builder for a property having as value a list of {@code <T>}. The
     * format of the individual items is the same as for {@linkplain #enumProperty(String, Map)}.
     *
     * @param name        Name of the property to build
     * @param nameToValue Map of labels to values. The null key is ignored.
     * @param <T>         Value type of the property
     *
     * @return A new builder
     */
    public static <T> GenericCollectionPropertyBuilder<T, List<T>> enumListProperty(String name, Map<String, T> nameToValue) {
        return enumProperty(name, nameToValue).toList();
    }


    /**
     * Returns a builder for a property having as value a list of {@code <T>}. The
     * format of the individual items is the same as for {@linkplain #enumProperty(String, Map)}.
     *
     * @param name       Name of the property to build
     * @param enumClass  Class of the values
     * @param labelMaker Function that associates enum constants to their label
     * @param <T>        Value type of the property
     *
     * @return A new builder
     */
    public static <T extends Enum<T>> GenericCollectionPropertyBuilder<T, List<T>> enumListProperty(String name, Class<T> enumClass, Function<? super T, String> labelMaker) {
        Map<String, T> enumMap = CollectionUtil.associateBy(asList(enumClass.getEnumConstants()), labelMaker);
        return enumListProperty(name, enumMap);
    }


}
