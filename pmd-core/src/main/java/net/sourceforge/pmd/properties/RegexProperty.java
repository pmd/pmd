/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.regex.Pattern;

import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper.SingleValue;
import net.sourceforge.pmd.properties.builders.SingleValuePropertyBuilder;


/**
 * Property which has a regex pattern as a value. This property has no multi-valued
 * variant, since it would be ambiguous whether the delimiters are part of the regex
 * or not.
 *
 * @author Clément Fournier
 * @since 6.2.0
 * @deprecated Use a {@code PropertyDescriptor<Pattern>}. A builder is available from {@link PropertyFactory#regexProperty(String)}.
 * This class will be removed in 7.0.0.
 */
@Deprecated
public final class RegexProperty extends AbstractSingleValueProperty<Pattern> {

    RegexProperty(String theName, String theDescription, Pattern theDefault, float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, theDefault, theUIOrder, isDefinedExternally);
    }


    @Override
    protected Pattern createFrom(String toParse) {
        return Pattern.compile(toParse);
    }


    @Override
    public Class<Pattern> type() {
        return Pattern.class;
    }


    static SingleValue<Pattern, RegexPBuilder> extractor() {
        return new PropertyDescriptorBuilderConversionWrapper.SingleValue<Pattern, RegexPBuilder>(Pattern.class, ValueParserConstants.REGEX_PARSER) {
            @Override
            protected RegexPBuilder newBuilder(String name) {
                return new RegexPBuilder(name);
            }
        };
    }


    /**
     * Creates a new builder for a regex property.
     *
     * @param name The name of the property
     *
     * @return A new builder
     *
     * @deprecated Use {@link PropertyFactory#regexProperty(String)}
     */
    @Deprecated
    public static RegexPBuilder named(String name) {
        return new RegexPBuilder(name);
    }


    /**
     * Builder for a {@link RegexProperty}.
     *
     * @deprecated Use {@link PropertyFactory#regexProperty(String)}
     */
    @Deprecated
    public static final class RegexPBuilder extends SingleValuePropertyBuilder<Pattern, RegexProperty.RegexPBuilder> {
        private RegexPBuilder(String name) {
            super(name);
        }


        /**
         * Specify a default pattern for the property.
         * The argument must be a valid regex pattern.
         *
         * @param val Regex pattern
         *
         * @return The same builder
         */
        public RegexPBuilder defaultValue(String val) {
            return super.defaultValue(Pattern.compile(val));
        }


        @Override
        public RegexProperty build() {
            return new RegexProperty(this.name, this.description, this.defaultValue, this.uiOrder, isDefinedInXML);
        }
    }
}
