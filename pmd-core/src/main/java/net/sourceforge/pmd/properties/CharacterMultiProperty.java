/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.properties.builders.MultiValuePropertyBuilder;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper;


/**
 * Multi-valued character property.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 * @deprecated Use a {@code PropertyDescriptor<List<Character>>}. A builder is available from {@link PropertyFactory#charListProperty(String)}.
 * This class will be removed in 7.0.0.
 */
@Deprecated
public final class CharacterMultiProperty extends AbstractMultiValueProperty<Character> {


    /**
     * Constructor using an array of defaults.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param defaultValues  Array of defaults
     * @param theUIOrder     UI order
     * @param delimiter      The delimiter to use
     *
     * @throws IllegalArgumentException if the delimiter is in the default values
     * @deprecated Use {@link PropertyFactory#charListProperty(String)}
     */
    @Deprecated
    public CharacterMultiProperty(String theName, String theDescription, Character[] defaultValues, float theUIOrder, char delimiter) {
        this(theName, theDescription, Arrays.asList(defaultValues), theUIOrder, delimiter, false);
    }


    /** Master constructor. */
    private CharacterMultiProperty(String theName, String theDescription, List<Character> defaultValues, float theUIOrder,
                                   char delimiter, boolean isDefinedExternally) {
        super(theName, theDescription, defaultValues, theUIOrder, delimiter, isDefinedExternally);

        if (defaultValues != null) {
            for (Character c : defaultValues) {
                if (c == delimiter) {
                    throw new IllegalArgumentException("Cannot include the delimiter in the set of defaults");
                }
            }
        }
    }


    /**
     * Constructor using a list of defaults.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param defaultValues  List of defaults
     * @param theUIOrder     UI order
     * @param delimiter      The delimiter to use
     *
     * @throws IllegalArgumentException if the delimiter is in the default values
     * @deprecated Use {@link PropertyFactory#charListProperty(String)}
     */
    @Deprecated
    public CharacterMultiProperty(String theName, String theDescription, List<Character> defaultValues, float theUIOrder, char delimiter) {
        this(theName, theDescription, defaultValues, theUIOrder, delimiter, false);
    }


    @Override
    protected Character createFrom(String toParse) {
        return CharacterProperty.charFrom(toParse);
    }


    @Override
    public Class<Character> type() {
        return Character.class;
    }


    @Override
    public List<Character> valueFrom(String valueString) throws IllegalArgumentException {
        String[] values = StringUtils.split(valueString, multiValueDelimiter());

        List<Character> chars = new ArrayList<>(values.length);
        for (int i = 0; i < values.length; i++) {
            chars.add(values[i].charAt(0));
        }
        return chars;
    }


    static PropertyDescriptorBuilderConversionWrapper.MultiValue<Character, CharacterMultiPBuilder> extractor() {
        return new PropertyDescriptorBuilderConversionWrapper.MultiValue<Character, CharacterMultiPBuilder>(Character.class, ValueParserConstants.CHARACTER_PARSER) {
            @Override
            protected CharacterMultiPBuilder newBuilder(String name) {
                return new CharacterMultiPBuilder(name);
            }
        };
    }


    /**
     * @deprecated Use {@link PropertyFactory#charListProperty(String)}
     */
    @Deprecated
    public static CharacterMultiPBuilder named(String name) {
        return new CharacterMultiPBuilder(name);
    }


    /**
     * @deprecated Use {@link PropertyFactory#charListProperty(String)}
     */
    @Deprecated
    public static final class CharacterMultiPBuilder extends MultiValuePropertyBuilder<Character, CharacterMultiPBuilder> {
        private CharacterMultiPBuilder(String name) {
            super(name);
        }


        @Override
        public CharacterMultiProperty build() {
            return new CharacterMultiProperty(this.name, this.description, this.defaultValues, this.uiOrder, multiValueDelimiter, isDefinedInXML);
        }
    }


}
