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
 * Defines a datatype that supports multiple Long property values within an
 * upper and lower boundary.
 *
 * @author Brian Remedios
 */
public class LongMultiProperty extends AbstractMultiNumericProperty<Long> {

    public static final PropertyDescriptorFactory FACTORY
        = new BasicPropertyDescriptorFactory<Long>(Long.class, NUMBER_FIELD_TYPES_BY_KEY) {

        @Override
        public LongMultiProperty createWith(Map<String, String> valuesById) {
            String[] minMax = minMaxFrom(valuesById);
            char delimiter = delimiterIn(valuesById, DEFAULT_NUMERIC_DELIMITER);
            Long[] defaultValues = longsIn(defaultValueIn(valuesById), delimiter);
            return new LongMultiProperty(nameIn(valuesById), descriptionIn(valuesById), Long.parseLong(minMax[0]),
                                         Long.parseLong(minMax[1]), defaultValues, 0f);
        }
    };

    /**
     * Constructor for LongProperty.
     *
     * @param theName        String
     * @param theDescription String
     * @param min            Long
     * @param max            Long
     * @param theDefaults    Long[]
     * @param theUIOrder     float
     *
     * @throws IllegalArgumentException
     */
    public LongMultiProperty(String theName, String theDescription, Long min, Long max, Long[] theDefaults,
                             float theUIOrder) {
        super(theName, theDescription, min, max, Arrays.asList(theDefaults), theUIOrder);
    }

    /**
     * Constructor for LongProperty.
     *
     * @param theName        String
     * @param theDescription String
     * @param min            Long
     * @param max            Long
     * @param theDefaults    Long[]
     * @param theUIOrder     float
     *
     * @throws IllegalArgumentException
     */
    public LongMultiProperty(String theName, String theDescription, Long min, Long max, List<Long> theDefaults,
                             float theUIOrder) {
        super(theName, theDescription, min, max, theDefaults, theUIOrder);
    }

    @Override
    public Class<Long> type() {
        return Long.class;
    }

    @Override
    protected Long createFrom(String value) {
        return Long.valueOf(value);
    }

}
