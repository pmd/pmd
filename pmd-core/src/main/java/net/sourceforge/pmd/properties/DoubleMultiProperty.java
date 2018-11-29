/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.properties.builders.MultiNumericPropertyBuilder;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper;


/**
 * Multi-valued double property.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 *
 * @deprecated Use a {@code PropertyDescriptor<List<Double>>} instead. A builder is available from {@link PropertyFactory#doubleListProperty(String)}.
 *             This class will be removed in 7.0.0.
 */
@Deprecated
public final class DoubleMultiProperty extends AbstractMultiNumericProperty<Double> {

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
     * @throws IllegalArgumentException if {@literal min > max} or one of the defaults is not between the bounds
     * @deprecated use {@link PropertyFactory#doubleListProperty(String)}
     */
    @Deprecated
    public DoubleMultiProperty(String theName, String theDescription, Double min, Double max,
                               Double[] defaultValues, float theUIOrder) {
        this(theName, theDescription, min, max, Arrays.asList(defaultValues), theUIOrder, false);
    }


    /** Master constructor. */
    private DoubleMultiProperty(String theName, String theDescription, Double min, Double max,
                                List<Double> defaultValues, float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, min, max, defaultValues, theUIOrder, isDefinedExternally);
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
     * @throws IllegalArgumentException if {@literal min > max} or one of the defaults is not between the bounds
     * @deprecated use {@link PropertyFactory#doubleListProperty(String)}
     */
    @Deprecated
    public DoubleMultiProperty(String theName, String theDescription, Double min, Double max,
                               List<Double> defaultValues, float theUIOrder) {
        this(theName, theDescription, min, max, defaultValues, theUIOrder, false);
    }


    @Override
    public Class<Double> type() {
        return Double.class;
    }


    @Override
    protected Double createFrom(String value) {
        return Double.valueOf(value);
    }


    static PropertyDescriptorBuilderConversionWrapper.MultiValue.Numeric<Double, DoubleMultiPBuilder> extractor() {
        return new PropertyDescriptorBuilderConversionWrapper.MultiValue.Numeric<Double, DoubleMultiPBuilder>(Double.class, ValueParserConstants.DOUBLE_PARSER) {
            @Override
            protected DoubleMultiPBuilder newBuilder(String name) {
                return new DoubleMultiPBuilder(name);
            }
        };
    }

    /** @deprecated use {@link PropertyFactory#doubleListProperty(String)} */
    @Deprecated
    public static DoubleMultiPBuilder named(String name) {
        return new DoubleMultiPBuilder(name);
    }


    /** @deprecated use {@link PropertyFactory#doubleListProperty(String)} */
    @Deprecated
    public static final class DoubleMultiPBuilder extends MultiNumericPropertyBuilder<Double, DoubleMultiPBuilder> {
        private DoubleMultiPBuilder(String name) {
            super(name);
        }


        @Override
        public DoubleMultiProperty build() {
            return new DoubleMultiProperty(name, description, lowerLimit, upperLimit, defaultValues, uiOrder, isDefinedInXML);
        }
    }

}
