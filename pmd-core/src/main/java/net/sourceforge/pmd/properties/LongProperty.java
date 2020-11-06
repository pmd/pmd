/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper;
import net.sourceforge.pmd.properties.builders.SingleNumericPropertyBuilder;


/**
 * Single valued long property.
 *
 * @author Brian Remedios
 * @author Clément Fournier
 * @version Refactored June 2017 (6.0.0)
 *
 * @deprecated Use a {@code PropertyDescriptor<Long>} instead. A builder is available from {@link PropertyFactory#longIntProperty(String)}.
 *             This class will be removed in 7.0.0.
 */
@Deprecated
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
     *
     * @throws IllegalArgumentException if {@literal min > max} or one of the defaults is not between the bounds
     * @deprecated Use {@link PropertyFactory#longIntProperty(String)}
     */
    @Deprecated
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
     *
     * @throws IllegalArgumentException if {@literal min > max} or one of the defaults is not between the bounds
     * @deprecated Use {@link PropertyFactory#longIntProperty(String)}
     */
    @Deprecated
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


    static PropertyDescriptorBuilderConversionWrapper.SingleValue.Numeric<Long, LongPBuilder> extractor() {
        return new PropertyDescriptorBuilderConversionWrapper.SingleValue.Numeric<Long, LongPBuilder>(Long.class, ValueParserConstants.LONG_PARSER) {
            @Override
            protected LongPBuilder newBuilder(String name) {
                return new LongPBuilder(name);
            }
        };
    }


    /** @deprecated Use {@link PropertyFactory#longIntProperty(String)} */
    @Deprecated
    public static LongPBuilder named(String name) {
        return new LongPBuilder(name);
    }


    /** @deprecated Use {@link PropertyFactory#longIntProperty(String)} */
    @Deprecated
    public static final class LongPBuilder extends SingleNumericPropertyBuilder<Long, LongPBuilder> {
        private LongPBuilder(String name) {
            super(name);
        }


        @Override
        public LongProperty build() {
            return new LongProperty(name, description, lowerLimit, upperLimit, defaultValue, uiOrder, isDefinedInXML);
        }
    }

}
