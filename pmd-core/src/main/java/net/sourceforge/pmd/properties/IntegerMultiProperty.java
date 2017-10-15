/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.properties.builders.MultiNumericPropertyBuilder;
import net.sourceforge.pmd.properties.builders.PropertyBuilderConversionWrapper;


/**
 * Multi-valued integer property.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class IntegerMultiProperty extends AbstractMultiNumericProperty<Integer> {


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
    public IntegerMultiProperty(String theName, String theDescription, Integer min, Integer max,
                                Integer[] defaultValues, float theUIOrder) {
        this(theName, theDescription, min, max, Arrays.asList(defaultValues), theUIOrder, false);
    }


    /** Master constructor. */
    private IntegerMultiProperty(String theName, String theDescription, Integer min, Integer max,
                                 List<Integer> defaultValues, float theUIOrder, boolean isDefinedExternally) {

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
    public IntegerMultiProperty(String theName, String theDescription, Integer min, Integer max,
                                List<Integer> defaultValues, float theUIOrder) {

        this(theName, theDescription, min, max, defaultValues, theUIOrder, false);
    }


    @Override
    public Class<Integer> type() {
        return Integer.class;
    }


    @Override
    protected Integer createFrom(String toParse) {
        return Integer.valueOf(toParse);
    }


    static PropertyBuilderConversionWrapper.MultiValue.Numeric<Integer, IntegerMultiPBuilder> extractor() {
        return new PropertyBuilderConversionWrapper.MultiValue.Numeric<Integer, IntegerMultiPBuilder>(ValueParserConstants.INTEGER_PARSER) {
            @Override
            protected IntegerMultiPBuilder newBuilder() {
                return new IntegerMultiPBuilder();
            }
        };
    }


    public static IntegerMultiPBuilder builder(String name) {
        return new IntegerMultiPBuilder().name(name);
    }


    private static class IntegerMultiPBuilder extends MultiNumericPropertyBuilder<Integer, IntegerMultiPBuilder> {

        @Override
        protected PropertyDescriptor<List<Integer>> createInstance() {
            return new IntegerMultiProperty(name, description, lowerLimit, upperLimit, defaultValues, uiOrder, isDefinedInXML);
        }
    }
}
