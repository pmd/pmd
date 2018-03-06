/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.regex.Pattern;

import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper.SingleValue;
import net.sourceforge.pmd.properties.builders.SingleValuePropertyBuilder;


/**
 * Property which has a regex pattern as a value.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
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
     */
    public static RegexPBuilder named(String name) {
        return new RegexPBuilder(name);
    }

    /** Builder for a {@link RegexProperty}. */
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
