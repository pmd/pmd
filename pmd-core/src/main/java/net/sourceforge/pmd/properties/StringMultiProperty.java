/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.properties.builders.MultiValuePropertyBuilder;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper;


/**
 * Defines a datatype that supports multiple String values. Note that all strings must be filtered by the delimiter
 * character.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 * @deprecated Use a {@code PropertyDescriptor<List<String>>}. A builder is available from {@link PropertyFactory#stringListProperty(String)}.
 * This class will be removed in 7.0.0.
 */
@Deprecated
public final class StringMultiProperty extends AbstractMultiValueProperty<String> {


    /**
     * Constructor using an array of defaults.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param defaultValues  Array of defaults
     * @param theUIOrder     UI order
     * @param delimiter      The delimiter to use
     *
     * @throws IllegalArgumentException if a default value contains the delimiter
     * @throws NullPointerException     if the defaults array is null
     * @deprecated Use {@link PropertyFactory#stringListProperty(String)}
     */
    @Deprecated
    public StringMultiProperty(String theName, String theDescription, String[] defaultValues, float theUIOrder,
                               char delimiter) {
        this(theName, theDescription, Arrays.asList(defaultValues), theUIOrder, delimiter);
    }


    /**
     * Constructor using a list of defaults.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param defaultValues  List of defaults
     * @param theUIOrder     UI order
     * @param delimiter      The delimiter to useg
     *
     * @throws IllegalArgumentException if a default value contains the delimiter
     * @throws NullPointerException     if the defaults array is null
     * @deprecated Use {@link PropertyFactory#stringListProperty(String)}
     */
    @Deprecated
    public StringMultiProperty(String theName, String theDescription, List<String> defaultValues, float theUIOrder,
                               char delimiter) {
        this(theName, theDescription, defaultValues, theUIOrder, delimiter, false);
    }


    /** Master constructor. */
    private StringMultiProperty(String theName, String theDescription, List<String> defaultValues, float theUIOrder,
                                char delimiter, boolean isDefinedExternally) {
        super(theName, theDescription, defaultValues, theUIOrder, delimiter, isDefinedExternally);

        checkDefaults(defaultValues, delimiter);
    }


    @Override
    public Class<String> type() {
        return String.class;
    }


    @Override
    public List<String> valueFrom(String valueString) {
        return Arrays.asList(StringUtils.split(valueString, multiValueDelimiter()));
    }


    @Override
    protected String valueErrorFor(String value) {

        if (value == null) {
            return "Missing value";
        }

        if (containsDelimiter(value)) {
            return "Value cannot contain the '" + multiValueDelimiter() + "' character";
        }

        // TODO - eval against regex checkers

        return null;
    }


    /**
     * Returns true if the multi value delimiter is present in the string.
     *
     * @param value String
     *
     * @return boolean
     */
    private boolean containsDelimiter(String value) {
        return value.indexOf(multiValueDelimiter()) >= 0;
    }


    @Override
    protected String createFrom(String toParse) {
        return toParse;
    }


    /**
     * Checks if the values are valid.
     *
     * @param defaultValue The default value
     * @param delim        The delimiter
     *
     * @throws IllegalArgumentException if one value contains the delimiter
     */
    private static void checkDefaults(List<String> defaultValue, char delim) {

        if (defaultValue == null) {
            return;
        }

        for (String aDefaultValue : defaultValue) {
            if (aDefaultValue.indexOf(delim) >= 0) {
                throw new IllegalArgumentException("Cannot include the delimiter in the set of defaults");
            }
        }
    }


    static PropertyDescriptorBuilderConversionWrapper.MultiValue<String, StringMultiPBuilder> extractor() {
        return new PropertyDescriptorBuilderConversionWrapper.MultiValue<String, StringMultiPBuilder>(String.class, ValueParserConstants.STRING_PARSER) {
            @Override
            protected StringMultiPBuilder newBuilder(String name) {
                return new StringMultiPBuilder(name);
            }
        };
    }


    /**
     * @deprecated Use {@link PropertyFactory#stringListProperty(String)}
     */
    @Deprecated
    public static StringMultiPBuilder named(String name) {
        return new StringMultiPBuilder(name);
    }


    /**
     * @deprecated Use {@link PropertyFactory#stringListProperty(String)}
     */
    @Deprecated
    public static final class StringMultiPBuilder extends MultiValuePropertyBuilder<String, StringMultiPBuilder> {
        private StringMultiPBuilder(String name) {
            super(name);
        }


        @Override
        public StringMultiProperty build() {
            return new StringMultiProperty(this.name, this.description, this.defaultValues, this.uiOrder, this.multiValueDelimiter, isDefinedInXML);
        }
    }
}
