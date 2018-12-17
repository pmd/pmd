/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.properties.builders.MultiNumericPropertyBuilder;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper;


/**
 * Multi-valued float property.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 * @deprecated Use a {@code PropertyDescriptor<List<Double>>} instead. A builder is available from {@link PropertyFactory#doubleListProperty(String)}.
 *             This class will be removed in 7.0.0.
 */
@Deprecated
public final class FloatMultiProperty extends AbstractMultiNumericProperty<Float> {


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
    public FloatMultiProperty(String theName, String theDescription, Float min, Float max,
                              Float[] defaultValues, float theUIOrder) {
        this(theName, theDescription, min, max, Arrays.asList(defaultValues), theUIOrder, false);
    }


    /** Master constructor. */
    private FloatMultiProperty(String theName, String theDescription, Float min, Float max,
                               List<Float> defaultValues, float theUIOrder, boolean isDefinedExternally) {
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
    public FloatMultiProperty(String theName, String theDescription, Float min, Float max,
                              List<Float> defaultValues, float theUIOrder) {
        this(theName, theDescription, min, max, defaultValues, theUIOrder, false);
    }


    @Override
    public Class<Float> type() {
        return Float.class;
    }


    @Override
    protected Float createFrom(String value) {
        return Float.valueOf(value);
    }


    static PropertyDescriptorBuilderConversionWrapper.MultiValue.Numeric<Float, FloatMultiPBuilder> extractor() {
        return new PropertyDescriptorBuilderConversionWrapper.MultiValue.Numeric<Float, FloatMultiPBuilder>(Float.class, ValueParserConstants.FLOAT_PARSER) {
            @Override
            protected FloatMultiPBuilder newBuilder(String name) {
                return new FloatMultiPBuilder(name);
            }
        };
    }


    /** @deprecated use {@link PropertyFactory#doubleListProperty(String)} */
    @Deprecated
    public static FloatMultiPBuilder named(String name) {
        return new FloatMultiPBuilder(name);
    }


    /** @deprecated use {@link PropertyFactory#doubleListProperty(String)} */
    @Deprecated
    public static final class FloatMultiPBuilder extends MultiNumericPropertyBuilder<Float, FloatMultiPBuilder> {
        private FloatMultiPBuilder(String name) {
            super(name);
        }


        @Override
        public FloatMultiProperty build() {
            return new FloatMultiProperty(name, description, lowerLimit, upperLimit, defaultValues, uiOrder, isDefinedInXML);
        }
    }


}
