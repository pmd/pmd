/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.properties.builders.PropertyBuilderConversionWrapper;
import net.sourceforge.pmd.properties.builders.SingleNumericPropertyBuilder;


/**
 * Single valued long property.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class LongProperty extends AbstractNumericProperty<Long> {


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
     * @throws IllegalArgumentException if min > max or one of the defaults is not between the bounds
     * @deprecated will be removed in 7.0.0
     */
    public LongProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr,
                        float theUIOrder) {
        this(theName, theDescription, Long.valueOf(minStr), Long.valueOf(maxStr),
                Long.valueOf(defaultStr), theUIOrder, false);
    }


    /** Master constructor. */
    private LongProperty(String theName, String theDescription, Long min, Long max, Long theDefault,
                         float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, min, max, theDefault, theUIOrder, isDefinedExternally);
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
     * @throws IllegalArgumentException if min > max or one of the defaults is not between the bounds
     */
    public LongProperty(String theName, String theDescription, Long min, Long max, Long theDefault, float theUIOrder) {
        this(theName, theDescription, min, max, theDefault, theUIOrder, false);
    }


    @Override
    public Class<Long> type() {
        return Long.class;
    }


    @Override
    protected Long createFrom(String toParse) {
        return Long.valueOf(toParse);
    }


    static PropertyBuilderConversionWrapper.SingleValue.Numeric<Long, LongPBuilder> extractor() {
        return new PropertyBuilderConversionWrapper.SingleValue.Numeric<Long, LongPBuilder>(Long.class, ValueParserConstants.LONG_PARSER) {
            @Override
            protected LongPBuilder newBuilder() {
                return new LongPBuilder();
            }
        };
    }


    public static LongPBuilder builder(String name) {
        return new LongPBuilder().name(name);
    }


    public static final class LongPBuilder extends SingleNumericPropertyBuilder<Long, LongPBuilder> {
        private LongPBuilder() {
        }


        @Override
        public LongProperty build() {
            return new LongProperty(name, description, lowerLimit, upperLimit, defaultValue, uiOrder, isDefinedInXML);
        }
    }

}
