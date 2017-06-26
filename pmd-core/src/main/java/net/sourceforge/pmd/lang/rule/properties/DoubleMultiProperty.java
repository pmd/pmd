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
 * Defines a property type that supports multiple double-type property values
 * within an upper and lower boundary.
 *
 * @author Brian Remedios
 */
public class DoubleMultiProperty extends AbstractMultiNumericProperty<Double> {

    public static final PropertyDescriptorFactory FACTORY
        = new BasicPropertyDescriptorFactory<List<Double>>(Double.class, NUMBER_FIELD_TYPES_BY_KEY) {

        @Override
        public DoubleMultiProperty createWith(Map<String, String> valuesById) {
            String[] minMax = minMaxFrom(valuesById);
            char delimiter = delimiterIn(valuesById, DEFAULT_NUMERIC_DELIMITER);
            Double[] defaultValues = doublesIn(numericDefaultValueIn(valuesById), delimiter);
            return new DoubleMultiProperty(nameIn(valuesById), descriptionIn(valuesById), Double.parseDouble(minMax[0]),
                                           Double.parseDouble(minMax[1]), defaultValues, 0f);
        }
    };

    /**
     * Constructor for DoubleProperty.
     *
     * @param theName        String
     * @param theDescription String
     * @param min            Double
     * @param max            Double
     * @param defaultValues  Double[]
     * @param theUIOrder     float
     */
    public DoubleMultiProperty(String theName, String theDescription, Double min, Double max, Double[] defaultValues,
                               float theUIOrder) {
        this(theName, theDescription, min, max, Arrays.asList(defaultValues), theUIOrder);
    }

    /**
     * Constructor for DoubleProperty.
     *
     * @param theName        String
     * @param theDescription String
     * @param min            Double
     * @param max            Double
     * @param defaultValues  Double[]
     * @param theUIOrder     float
     */
    public DoubleMultiProperty(String theName, String theDescription, Double min, Double max, List<Double> defaultValues,
                               float theUIOrder) {
        super(theName, theDescription, min, max, defaultValues, theUIOrder);
    }

    @Override
    public Class<Double> type() {
        return Double.class;
    }

    @Override
    protected Double createFrom(String value) {
        return Double.valueOf(value);
    }

}
