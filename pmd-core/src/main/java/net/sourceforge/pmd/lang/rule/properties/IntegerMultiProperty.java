/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.lang.rule.properties.AbstractNumericProperty.NUMBER_FIELD_TYPES_BY_KEY;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a datatype that supports multiple Integer property values within an
 * upper and lower boundary.
 *
 * @author Brian Remedios
 */
public class IntegerMultiProperty extends AbstractMultiNumericProperty<Integer> {

    public static final PropertyDescriptorFactory FACTORY
        = new BasicPropertyDescriptorFactory<List<Integer>>(Integer.class, NUMBER_FIELD_TYPES_BY_KEY) {

        @Override
        public IntegerMultiProperty createWith(Map<String, String> valuesById) {
            String[] minMax = minMaxFrom(valuesById);
            char delimiter = delimiterIn(valuesById, DEFAULT_NUMERIC_DELIMITER);
            Integer[] defaultValues = parsePrimitives(numericDefaultValueIn(valuesById), delimiter, PrimitiveExtractor.INTEGER_EXTRACTOR);
            return new IntegerMultiProperty(nameIn(valuesById), descriptionIn(valuesById), Integer.parseInt(minMax[0]),
                                            Integer.parseInt(minMax[1]), defaultValues, 0f);
        }
    };

    /**
     * Constructor for IntegerProperty.
     *
     * @param theName        String
     * @param theDescription String
     * @param min            Integer
     * @param max            Integer
     * @param theDefaults    Integer[]
     * @param theUIOrder     float
     *
     * @throws IllegalArgumentException
     */
    public IntegerMultiProperty(String theName, String theDescription, Integer min, Integer max, Integer[] theDefaults,
                                float theUIOrder) {

        this(theName, theDescription, min, max, Arrays.asList(theDefaults), theUIOrder);
    }

    /**
     * Constructor for IntegerProperty.
     *
     * @param theName        String
     * @param theDescription String
     * @param min            Integer
     * @param max            Integer
     * @param theDefaults    Integer[]
     * @param theUIOrder     float
     *
     * @throws IllegalArgumentException
     */
    public IntegerMultiProperty(String theName, String theDescription, Integer min, Integer max,
                                List<Integer> theDefaults, float theUIOrder) {

        super(theName, theDescription, min, max, theDefaults, theUIOrder);
    }


    @Override
    public Class<Integer> type() {
        return Integer.class;
    }

    @Override
    protected Integer createFrom(String toParse) {
        return Integer.valueOf(toParse);
    }
}
