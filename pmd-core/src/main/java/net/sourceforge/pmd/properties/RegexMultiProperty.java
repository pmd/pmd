/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.pmd.properties.builders.MultiValuePropertyBuilder;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper.MultiValue;


/**
 * Property which takes a collection of regex patterns as its value.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public final class RegexMultiProperty extends AbstractMultiValueProperty<Pattern> {
    RegexMultiProperty(String theName, String theDescription, List<Pattern> theDefault, float theUIOrder, char delimiter, boolean isDefinedExternally) {
        super(theName, theDescription, theDefault, theUIOrder, delimiter, isDefinedExternally);
    }


    @Override
    protected Pattern createFrom(String toParse) {
        return Pattern.compile(toParse);
    }


    @Override
    public Class<Pattern> type() {
        return Pattern.class;
    }


    static MultiValue<Pattern, RegexMultiPBuilder> extractor() {
        return new MultiValue<Pattern, RegexMultiPBuilder>(Pattern.class, ValueParserConstants.REGEX_PARSER) {
            @Override
            protected RegexMultiPBuilder newBuilder(String name) {
                return new RegexMultiPBuilder(name);
            }
        };
    }


    /**
     * Creates a new builder for a regex multi property.
     *
     * @param name The name of the property
     *
     * @return A new builder
     */
    public static RegexMultiPBuilder named(String name) {
        return new RegexMultiPBuilder(name);
    }


    /** Builder for a {@link RegexMultiProperty}. */
    public static final class RegexMultiPBuilder extends MultiValuePropertyBuilder<Pattern, RegexMultiPBuilder> {
        private RegexMultiPBuilder(String name) {
            super(name);
        }


        /**
         * Specify default patterns for the property.
         * The arguments must be valid regex patterns.
         *
         * @param val Regex patterns
         *
         * @return The same builder
         */
        public RegexMultiPBuilder defaultValues(String... val) {
            List<Pattern> ps = new ArrayList<>();
            for (String s : val) {
                ps.add(Pattern.compile(s));
            }
            return super.defaultValues(ps);
        }


        @Override
        public RegexMultiProperty build() {
            return new RegexMultiProperty(this.name, this.description, this.defaultValues, this.uiOrder, this.multiValueDelimiter, isDefinedInXML);
        }
    }
}
