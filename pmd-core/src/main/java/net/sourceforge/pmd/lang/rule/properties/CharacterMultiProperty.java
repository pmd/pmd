/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory.PrimitiveExtractor;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a property type that supports multiple Character values.
 *
 * @author Brian Remedios
 */
public class CharacterMultiProperty extends AbstractMultiValueProperty<Character> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<List<Character>>(Character.class) {

        @Override
        public CharacterMultiProperty createWith(Map<String, String> valuesById) {
            char delimiter = delimiterIn(valuesById);
            return new CharacterMultiProperty(nameIn(valuesById),
                                              descriptionIn(valuesById),
                                              parsePrimitives(defaultValueIn(valuesById), delimiter, PrimitiveExtractor.CHARACTER_EXTRACTOR),
                                              0.0f,
                                              delimiter);
        }
    };


    /**
     * Constructor for CharacterProperty.
     *
     * @param theName        String
     * @param theDescription String
     * @param theDefaults    char[]
     * @param theUIOrder     float
     * @param delimiter      char
     *
     * @throws IllegalArgumentException If the delimiter is in the default values
     */
    public CharacterMultiProperty(String theName, String theDescription, Character[] theDefaults, float theUIOrder,
                                  char delimiter) {
        super(theName, theDescription, theDefaults, theUIOrder, delimiter);

        if (theDefaults != null) {
            for (Character theDefault : theDefaults) {
                if (theDefault == delimiter) {
                    throw new IllegalArgumentException("Cannot include the delimiter in the set of defaults");
                }
            }
        }
    }

    @Override
    protected Character createFrom(String toParse) {
        return CharacterProperty.charFrom(toParse);
    }


    public CharacterMultiProperty(String theName, String theDescription, List<Character> theDefaults, float theUIOrder,
                                  char delimiter) {
        super(theName, theDescription, theDefaults, theUIOrder, delimiter);

        if (theDefaults != null) {
            for (Character c : theDefaults) {
                if (c == delimiter) {
                    throw new IllegalArgumentException("Cannot include the delimiter in the set of defaults");
                }
            }
        }
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
