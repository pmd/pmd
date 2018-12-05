/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.properties.PropertyBuilder.GenericCollectionPropertyBuilder;
import net.sourceforge.pmd.properties.PropertyBuilder.GenericPropertyBuilder;
import net.sourceforge.pmd.properties.PropertyBuilder.RegexPropertyBuilder;
import net.sourceforge.pmd.properties.constraints.NumericConstraints;
import net.sourceforge.pmd.properties.constraints.PropertyConstraint;

//@formatter:off
/**
 * Provides factory methods for common property types.
 * Note: from 7.0.0 on, this will be the only way to
 * build property descriptors. Concrete property classes
 * and their constructors/ builders will be gradually
 * deprecated before 7.0.0.
 *
 * <h1>Usage</h1>
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
 * <h1>Example</h1>
 *
 * <pre>
 * class MyRule {
 *   // The property descriptor may be static, it can be shared across threads.
 *   private static final {@link PropertyDescriptor}&lt;Integer> myIntProperty
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

    private PropertyFactory() {

    }


    /**
     * Returns a builder for an integer property. The property descriptor
     * will by default accept any value conforming to the format specified
     * by {@link Integer#parseInt(String)}, e.g. {@code 1234} or {@code -123}.
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
    public static GenericPropertyBuilder<Integer> intProperty(String name) {
        return new GenericPropertyBuilder<>(name, ValueParserConstants.INTEGER_PARSER, Integer.class);
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
        return intProperty(name).toList().delim(MultiValuePropertyDescriptor.DEFAULT_NUMERIC_DELIMITER);
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
        return new GenericPropertyBuilder<>(name, ValueParserConstants.DOUBLE_PARSER, Double.class);
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
        return doubleProperty(name).toList().delim(MultiValuePropertyDescriptor.DEFAULT_NUMERIC_DELIMITER);
    }


    /**
     * Returns a builder for a regex property. The value type of such
     * a property is {@link java.util.regex.Pattern}. For this use case, this type of
     * property should be preferred over {@linkplain #stringProperty(String) stringProperty}
     * as pattern compilation, including syntax errors, are handled transparently to
     * the rule.
     *
     * <p>This type of property is not available as a list, because the delimiters
     * could be part of the regex. This restriction will be lifted with 7.0.0.
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
     * character sequence '\' 'n' and not the line-feed character '\n'). This
     * behaviour could be changed with PMD 7.0.0.
     *
     * @param name Name of the property to build
     *
     * @return A new builder
     */
    public static GenericPropertyBuilder<String> stringProperty(String name) {
        return new GenericPropertyBuilder<>(name, ValueParserConstants.STRING_PARSER, String.class);
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
     * Returns a builder for a boolean property. The boolean is parsed from
     * the XML using {@link Boolean#valueOf(String)}, i.e. the only truthy
     * value is the string "true", and all other string values are falsy.
     *
     * @param name Name of the property to build
     *
     * @return A new builder
     */
    public static GenericPropertyBuilder<Boolean> booleanProperty(String name) {
        return new GenericPropertyBuilder<>(name, ValueParserConstants.BOOLEAN_PARSER, Boolean.class);
    }


    public static <T> GenericPropertyBuilder<T> enumProperty(String name, Map<String, T> nameToValue) {
        // TODO find solution to document the set of possible values
        // At best, map that requirement to a constraint (eg make parser return null if not found, and
        // add a non-null constraint with the right description.)
        return new GenericPropertyBuilder<>(name, ValueParserConstants.enumerationParser(nameToValue), (Class<T>) Object.class);
    }


    public static <T> GenericCollectionPropertyBuilder<T, List<T>> enumListProperty(String name, Map<String, T> nameToValue) {
        return enumProperty(name, nameToValue).toList().delim(MultiValuePropertyDescriptor.DEFAULT_DELIMITER);
    }


}
