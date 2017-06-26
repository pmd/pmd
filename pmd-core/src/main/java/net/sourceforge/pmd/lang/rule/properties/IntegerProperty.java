/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.lang.rule.properties.factories.ValueParser.INTEGER_PARSER;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a datatype that supports single Integer property values within an
 * upper and lower boundary.
 *
 * @author Brian Remedios
 */
public class IntegerProperty extends AbstractNumericProperty<Integer> {

    public static final PropertyDescriptorFactory FACTORY
        = new BasicPropertyDescriptorFactory<Integer>(Integer.class, NUMBER_FIELD_TYPES_BY_KEY) {

        @Override
        public IntegerProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
            final String[] minMax = minMaxFrom(valuesById);
            return new IntegerProperty(nameIn(valuesById),
                                       descriptionIn(valuesById),
                                       INTEGER_PARSER.valueOf(minMax[0]),
                                       INTEGER_PARSER.valueOf(minMax[1]),
                                       INTEGER_PARSER.valueOf(numericDefaultValueIn(valuesById)),
                                       0f);
        }
    };


    /**
     * Constructor for IntegerProperty that limits itself to a single value within the specified limits.
     *
     * @param theName        String
     * @param theDescription String
     * @param min            Integer
     * @param max            Integer
     * @param theDefault     Integer
     * @param theUIOrder     float
     *
     * @throws IllegalArgumentException
     */
    public IntegerProperty(String theName, String theDescription, int min, int max, int theDefault,
                           float theUIOrder) {
        super(theName, theDescription, min, max, theDefault, theUIOrder);
    }


    /**
     * Parses a String into an Integer.
     *
     * @param numberString String to parse
     *
     * @return Parsed Integer
     */
    public static Integer intFrom(String numberString) {

    }


    @Override
    public Class<Integer> type() {
        return Integer.class;
    }


    @Override
    protected Integer createFrom(String value) {
        return INTEGER_PARSER.valueOf(value);
    }
}
