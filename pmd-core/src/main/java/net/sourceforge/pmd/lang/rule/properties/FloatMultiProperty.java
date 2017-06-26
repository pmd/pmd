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
 * Defines a property type that support float property values within an upper
 * and lower boundary.
 *
 * @author Brian Remedios
 */
public class FloatMultiProperty extends AbstractMultiNumericProperty<Float> {

    public static final PropertyDescriptorFactory FACTORY
        = new BasicPropertyDescriptorFactory<Float>(Float.class, NUMBER_FIELD_TYPES_BY_KEY) {

        @Override
        public FloatMultiProperty createWith(Map<String, String> valuesById) {
            String[] minMax = minMaxFrom(valuesById);
            char delimiter = delimiterIn(valuesById, DEFAULT_NUMERIC_DELIMITER);
            Float[] defaultValues = floatsIn(numericDefaultValueIn(valuesById), delimiter);
            return new FloatMultiProperty(nameIn(valuesById), descriptionIn(valuesById), Float.parseFloat(minMax[0]),
                                          Float.parseFloat(minMax[1]), defaultValues, 0f);
        }
    };

    /**
     * Constructor for FloatProperty that configures it to accept multiple
     * values and any number of defaults.
     *
     * @param theName        String
     * @param theDescription String
     * @param min            Float
     * @param max            Float
     * @param defaultValues  Float[]
     * @param theUIOrder     float
     *
     * @throws IllegalArgumentException
     */
    public FloatMultiProperty(String theName, String theDescription, Float min, Float max, Float[] defaultValues,
                              float theUIOrder) {
        this(theName, theDescription, min, max, Arrays.asList(defaultValues), theUIOrder);
    }

    /**
     * Constructor for FloatProperty that configures it to accept multiple
     * values and any number of defaults.
     *
     * @param theName        String
     * @param theDescription String
     * @param min            Float
     * @param max            Float
     * @param defaultValues  Float[]
     * @param theUIOrder     float
     *
     * @throws IllegalArgumentException
     */
    public FloatMultiProperty(String theName, String theDescription, Float min, Float max,
                              List<Float> defaultValues, float theUIOrder) {
        super(theName, theDescription, min, max, defaultValues, theUIOrder);
    }


    @Override
    public Class<Float> type() {
        return Float.class;
    }


    @Override
    protected Float createFrom(String value) {
        return Float.valueOf(value);
    }
}
