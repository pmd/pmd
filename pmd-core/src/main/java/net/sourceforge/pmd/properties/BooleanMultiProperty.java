/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.properties.ValueParserConstants.BOOLEAN_PARSER;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.properties.builders.MultiValuePropertyBuilder;
import net.sourceforge.pmd.properties.builders.PropertyBuilderConversionWrapper;


/**
 * Defines a property type that supports multiple Boolean values.
 *
 * @author Brian Remedios
 */
public final class BooleanMultiProperty extends AbstractMultiValueProperty<Boolean> {


    /**
     * Constructor using an array of defaults.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param defaultValues  List of defaults
     * @param theUIOrder     UI order
     */
    public BooleanMultiProperty(String theName, String theDescription, Boolean[] defaultValues, float theUIOrder) {
        this(theName, theDescription, Arrays.asList(defaultValues), theUIOrder, false);
    }


    /** Master constructor. */
    private BooleanMultiProperty(String theName, String theDescription, List<Boolean> defaultValues,
                                 float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, defaultValues, theUIOrder, isDefinedExternally);
    }


    /**
     * Constructor using a list of defaults.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param defaultValues  List of defaults
     * @param theUIOrder     UI order
     */
    public BooleanMultiProperty(String theName, String theDescription, List<Boolean> defaultValues, float theUIOrder) {
        this(theName, theDescription, defaultValues, theUIOrder, false);
    }


    @Override
    protected Boolean createFrom(String toParse) {
        return BOOLEAN_PARSER.valueOf(toParse);
    }


    @Override
    public Class<Boolean> type() {
        return Boolean.class;
    }


    static PropertyBuilderConversionWrapper.MultiValue<Boolean, BooleanMultiPBuilder> extractor() {
        return new PropertyBuilderConversionWrapper.MultiValue<Boolean, BooleanMultiPBuilder>(Boolean.class, ValueParserConstants.BOOLEAN_PARSER) {
            @Override
            protected BooleanMultiPBuilder newBuilder() {
                return new BooleanMultiPBuilder();
            }
        };
    }


    public static BooleanMultiPBuilder builder(String name) {
        return new BooleanMultiPBuilder().name(name);
    }


    public static final class BooleanMultiPBuilder extends MultiValuePropertyBuilder<Boolean, BooleanMultiPBuilder> {
        private BooleanMultiPBuilder() {
        }


        @Override
        public BooleanMultiProperty build() {
            return new BooleanMultiProperty(this.name, this.description, this.defaultValues, this.uiOrder, isDefinedInXML);
        }
    }

}
