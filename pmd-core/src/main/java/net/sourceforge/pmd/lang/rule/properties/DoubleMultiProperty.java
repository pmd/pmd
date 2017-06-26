/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.lang.rule.properties.AbstractNumericProperty.NUMBER_FIELD_TYPES_BY_KEY;
import static net.sourceforge.pmd.lang.rule.properties.factories.ValueParser.DOUBLE_PARSER;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Multi-valued double property.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public class DoubleMultiProperty extends AbstractMultiNumericProperty<Double> {

    /** Factory. */
    public static final PropertyDescriptorFactory FACTORY
        = new BasicPropertyDescriptorFactory<List<Double>>(Double.class, NUMBER_FIELD_TYPES_BY_KEY) {
        @Override
        public DoubleMultiProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
            String[] minMax = minMaxFrom(valuesById);
            char delimiter = delimiterIn(valuesById, DEFAULT_NUMERIC_DELIMITER);
            List<Double> defaultValues = parsePrimitives(numericDefaultValueIn(valuesById), delimiter, DOUBLE_PARSER);
            return new DoubleMultiProperty(nameIn(valuesById),
                                           descriptionIn(valuesById),
                                           DOUBLE_PARSER.valueOf(minMax[0]),
                                           DOUBLE_PARSER.valueOf(minMax[1]),
                                           defaultValues,
                                           0f);
        }
    };


    /**
     * Constructor using an array of defaults.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param min            Minimum value of the property
     * @param max            Maximum value of the property
     * @param defaultValues  Array of defaults
     * @param theUIOrder     UI order
     *
     * @throws IllegalArgumentException if min > max or one of the defaults is not between the bounds
     */
    public DoubleMultiProperty(String theName, String theDescription, Double min, Double max,
                               Double[] defaultValues, float theUIOrder) {
        this(theName, theDescription, min, max, Arrays.asList(defaultValues), theUIOrder);
    }


    /**
     * Constructor using a list of defaults.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param min            Minimum value of the property
     * @param max            Maximum value of the property
     * @param defaultValues  List of defaults
     * @param theUIOrder     UI order
     *
     * @throws IllegalArgumentException if min > max or one of the defaults is not between the bounds
     */
    public DoubleMultiProperty(String theName, String theDescription, Double min, Double max,
                               List<Double> defaultValues, float theUIOrder) {
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
