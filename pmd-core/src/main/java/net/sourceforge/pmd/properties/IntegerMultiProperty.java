/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.properties.builders.MultiNumericPropertyBuilder;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper;


/**
 * Multi-valued integer property.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 *
 *
 * @deprecated Use a {@code PropertyDescriptor<List<Integer>>} instead. A builder is available from {@link PropertyFactory#intListProperty(String)}.
 *             This class will be removed in 7.0.0.
 */
@Deprecated
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
     *
     * @throws IllegalArgumentException if {@literal min > max} or one of the defaults is not between the bounds
     * @deprecated Use {@link PropertyFactory#intListProperty(String)}
     */
    @Deprecated
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
     *
     * @throws IllegalArgumentException if {@literal min > max} or one of the defaults is not between the bounds
     * @deprecated Use {@link PropertyFactory#intListProperty(String)}
     */
    @Deprecated
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


    static PropertyDescriptorBuilderConversionWrapper.MultiValue.Numeric<Integer, IntegerMultiPBuilder> extractor() {
        return new PropertyDescriptorBuilderConversionWrapper.MultiValue.Numeric<Integer, IntegerMultiPBuilder>(Integer.class, ValueParserConstants.INTEGER_PARSER) {
            @Override
            protected IntegerMultiPBuilder newBuilder(String name) {
                return new IntegerMultiPBuilder(name);
            }
        };
    }


    /**
     * @deprecated Use {@link PropertyFactory#intListProperty(String)}
     */
    @Deprecated
    public static IntegerMultiPBuilder named(String name) {
        return new IntegerMultiPBuilder(name);
    }


    /**
     * @deprecated Use {@link PropertyFactory#intListProperty(String)}
     */
    @Deprecated
    public static final class IntegerMultiPBuilder extends MultiNumericPropertyBuilder<Integer, IntegerMultiPBuilder> {
        private IntegerMultiPBuilder(String name) {
            super(name);
        }


        @Override
        public IntegerMultiProperty build() {
            return new IntegerMultiProperty(name, description, lowerLimit, upperLimit, defaultValues, uiOrder, isDefinedInXML);
        }
    }
}
