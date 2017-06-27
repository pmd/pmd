/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.ValueParser;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Multi-valued character property.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class CharacterMultiProperty extends AbstractMultiValueProperty<Character> {

    /** Factory. */
    public static final PropertyDescriptorFactory FACTORY // @formatter:off
        = new BasicPropertyDescriptorFactory<List<Character>>(Character.class) {
            @Override
            public CharacterMultiProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
                char delimiter = delimiterIn(valuesById);
                return new CharacterMultiProperty(nameIn(valuesById),
                                                  descriptionIn(valuesById),
                                                  parsePrimitives(defaultValueIn(valuesById), delimiter, ValueParser.CHARACTER_PARSER),
                                                  0.0f,
                                                  delimiter);
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
    public CharacterMultiProperty(String theName, String theDescription, Character[] defaultValues, float theUIOrder,
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
     * @param delimiter      The delimiter to use
     *
     * @throws IllegalArgumentException if the delimiter is in the default values
     */
    public CharacterMultiProperty(String theName, String theDescription, List<Character> defaultValues, float theUIOrder,
                                  char delimiter) {
        super(theName, theDescription, defaultValues, theUIOrder, delimiter);

        if (defaultValues != null) {
            for (Character c : defaultValues) {
                if (c == delimiter) {
                    throw new IllegalArgumentException("Cannot include the delimiter in the set of defaults");
                }
            }
        }
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
        String[] values = StringUtil.substringsOf(valueString, multiValueDelimiter());

        List<Character> chars = new ArrayList<>(values.length);
        for (int i = 0; i < values.length; i++) {
            chars.add(values[i].charAt(0));
        }
        return chars;
    }

}
