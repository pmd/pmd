/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.lang.rule.properties.factories.ValueParser.LONG_PARSER;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Single valued long property.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class LongProperty extends AbstractNumericProperty<Long> {

    /** Factory. */
    public static final PropertyDescriptorFactory FACTORY // @formatter:off
        = new BasicPropertyDescriptorFactory<Long>(Long.class, NUMBER_FIELD_TYPES_BY_KEY) {
            @Override
            public LongProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
                final String[] minMax = minMaxFrom(valuesById);
                return new LongProperty(nameIn(valuesById),
                                        descriptionIn(valuesById),
                                        LONG_PARSER.valueOf(minMax[0]),
                                        LONG_PARSER.valueOf(minMax[1]),
                                        LONG_PARSER.valueOf(numericDefaultValueIn(valuesById)), 0f);
            }
        };
    // @formatter:on


    /**
     * Constructor for LongProperty that limits itself to a single value within the specified limits. Converts string
     * arguments into the Long values.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param minStr         Minimum value of the property
     * @param maxStr         Maximum value of the property
     * @param defaultStr     Default value
     * @param theUIOrder     UI order
     *
     * @throws IllegalArgumentException if min > max or one of the defaults is not between the bounds
     * @deprecated ?
     */
    public LongProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr,
                        float theUIOrder) {
        this(theName, theDescription, longFrom(minStr), longFrom(maxStr), longFrom(defaultStr), theUIOrder);
    }


    /**
     * Constructor that limits itself to a single value within the specified limits.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param min            Minimum value of the property
     * @param max            Maximum value of the property
     * @param theDefault     Default value
     * @param theUIOrder     UI order
     *
     * @throws IllegalArgumentException if min > max or one of the defaults is not between the bounds
     */
    public LongProperty(String theName, String theDescription, Long min, Long max, Long theDefault, float theUIOrder) {
        super(theName, theDescription, min, max, theDefault, theUIOrder);
    }


    /**
     * Parses a String into a Long.
     *
     * @param numberString String to parse
     *
     * @return Parsed Long
     */
    public static Long longFrom(String numberString) {
        return Long.valueOf(numberString);
    }


    @Override
    public Class<Long> type() {
        return Long.class;
    }


    @Override
    protected Long createFrom(String toParse) {
        return Long.valueOf(toParse);
    }

}
