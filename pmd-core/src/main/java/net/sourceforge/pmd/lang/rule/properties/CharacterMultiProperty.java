/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.ValueParser.Companion;

/**
 * Multi-valued character property.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class CharacterMultiProperty extends AbstractMultiValueProperty<Character> {

    /** Factory. */
    public static final PropertyDescriptorFactory<List<Character>> FACTORY // @formatter:off
        = new MultiValuePropertyDescriptorFactory<Character>(Character.class) {

            @Override
            protected boolean isValueMissing(String value) {
                return StringUtils.isEmpty(value);
            }

            @Override
            public CharacterMultiProperty createWith(Map<PropertyDescriptorField, String> valuesById, boolean isDefinedExternally) {
                char delimiter = delimiterIn(valuesById);
                return new CharacterMultiProperty(nameIn(valuesById),
                                                  descriptionIn(valuesById),
                                                  Companion.parsePrimitives(defaultValueIn(valuesById), delimiter, ValueParser.CHARACTER_PARSER),
                                                  0.0f,
                                                  delimiter,
                                                  isDefinedExternally);
            }
        }; // @formatter:on


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
     */
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
     */
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

}
