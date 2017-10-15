/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.properties.builders.PropertyBuilderConversionWrapper;
import net.sourceforge.pmd.properties.builders.SingleValuePropertyBuilder;


/**
 * Defines a datatype that supports single String values.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class StringProperty extends AbstractSingleValueProperty<String> {

    /**
     * Constructor.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param defaultValue   Default value
     * @param theUIOrder     UI order
     */
    public StringProperty(String theName, String theDescription, String defaultValue, float theUIOrder) {
        this(theName, theDescription, defaultValue, theUIOrder, false);
    }


    /** Master constructor. */
    private StringProperty(String theName, String theDescription, String defaultValue, float theUIOrder, boolean
            isDefinedExternally) {
        super(theName, theDescription, defaultValue, theUIOrder, isDefinedExternally);
    }


    @Override
    public Class<String> type() {
        return String.class;
    }


    @Override
    public String createFrom(String valueString) {
        return valueString;
    }


    static PropertyBuilderConversionWrapper.SingleValue<String, StringPBuilder> extractor() {
        return new PropertyBuilderConversionWrapper.SingleValue<String, StringPBuilder>(String.class, ValueParserConstants.STRING_PARSER) {
            @Override
            protected StringPBuilder newBuilder() {
                return new StringPBuilder();
            }
        };
    }


    public static StringPBuilder builder(String name) {
        return new StringPBuilder().name(name);
    }


    public static final class StringPBuilder extends SingleValuePropertyBuilder<String, StringPBuilder> {
        private StringPBuilder() {
        }


        @Override
        public StringProperty build() {
            return new StringProperty(this.name, this.description, this.defaultValue, this.uiOrder, isDefinedInXML);
        }
    }
}
