/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.properties.builders.MultiNumericPropertyBuilder;
import net.sourceforge.pmd.properties.builders.PropertyBuilderConversionWrapper;


/**
 * Multi-valued float property.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
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
     * @throws IllegalArgumentException if min > max or one of the defaults is not between the bounds
     */
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
     * @throws IllegalArgumentException if min > max or one of the defaults is not between the bounds
     */
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


    static PropertyBuilderConversionWrapper.MultiValue.Numeric<Float, FloatMultiPBuilder> extractor() {
        return new PropertyBuilderConversionWrapper.MultiValue.Numeric<Float, FloatMultiPBuilder>(ValueParserConstants.FLOAT_PARSER) {
            @Override
            protected FloatMultiPBuilder newBuilder() {
                return new FloatMultiPBuilder();
            }
        };
    }


    public static FloatMultiPBuilder builder(String name) {
        return new FloatMultiPBuilder().name(name);
    }


    private static class FloatMultiPBuilder extends MultiNumericPropertyBuilder<Float, FloatMultiPBuilder> {

        @Override
        protected PropertyDescriptor<List<Float>> createInstance() {
            return new FloatMultiProperty(name, description, lowerLimit, upperLimit, defaultValues, uiOrder, isDefinedInXML);
        }
    }


}
