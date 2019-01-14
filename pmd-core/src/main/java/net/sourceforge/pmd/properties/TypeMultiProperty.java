/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;

import net.sourceforge.pmd.properties.builders.MultiPackagedPropertyBuilder;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper;
import net.sourceforge.pmd.properties.modules.TypePropertyModule;


/**
 * Defines a property that supports multiple class types, even for primitive values!
 *
 * TODO - untested for array types
 * @deprecated Will be removed with 7.0.0 with no scheduled replacement yet
 * @author Brian Remedios
 */
@Deprecated
public final class TypeMultiProperty extends AbstractMultiPackagedProperty<Class> {


    /**
     * Constructor for TypeProperty.
     *
     * @param theName           String
     * @param theDescription    String
     * @param theDefaults       Class[]
     * @param legalPackageNames String[]
     * @param theUIOrder        float
     *
     * @throws IllegalArgumentException
     */
    public TypeMultiProperty(String theName, String theDescription, List<Class> theDefaults,
                             String[] legalPackageNames, float theUIOrder) {
        this(theName, theDescription, theDefaults, legalPackageNames, theUIOrder, false);

    }


    /** Master constructor. */
    private TypeMultiProperty(String theName, String theDescription, List<Class> theTypeDefaults,
                              String[] legalPackageNames, float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, theTypeDefaults, theUIOrder, isDefinedExternally,
            new TypePropertyModule(legalPackageNames, theTypeDefaults));
    }


    /**
     * Constructor for TypeProperty.
     *
     * @param theName           String
     * @param theDescription    String
     * @param theTypeDefaults   String
     * @param legalPackageNames String[]
     * @param theUIOrder        float
     *
     * @throws IllegalArgumentException
     */
    public TypeMultiProperty(String theName, String theDescription, String theTypeDefaults,
                             String[] legalPackageNames, float theUIOrder) {
        this(theName, theDescription, typesFrom(theTypeDefaults),
            legalPackageNames,
            theUIOrder, false);

    }


    @Override
    public Class<Class> type() {
        return Class.class;
    }


    @Override
    public String asString(Class value) {
        return value == null ? "" : value.getName();
    }


    @Override
    protected Class createFrom(String toParse) {
        return ValueParserConstants.CLASS_PARSER.valueOf(toParse);
    }


    @Override
    public List<Class> valueFrom(String valueString) {
        return typesFrom(valueString);
    }


    private static List<Class> typesFrom(String valueString) {
        return ValueParserConstants.parsePrimitives(valueString, MULTI_VALUE_DELIMITER, ValueParserConstants.CLASS_PARSER);
    }


    public static PropertyDescriptorBuilderConversionWrapper.MultiValue.Packaged<Class, TypeMultiPBuilder> extractor() {
        return new PropertyDescriptorBuilderConversionWrapper.MultiValue.Packaged<Class, TypeMultiPBuilder>(Class.class, ValueParserConstants.CLASS_PARSER) {
            @Override
            protected TypeMultiPBuilder newBuilder(String name) {
                return new TypeMultiPBuilder(name);
            }
        };
    }


    public static TypeMultiPBuilder named(String name) {
        return new TypeMultiPBuilder(name);
    }


    public static final class TypeMultiPBuilder extends MultiPackagedPropertyBuilder<Class, TypeMultiPBuilder> {

        private TypeMultiPBuilder(String name) {
            super(name);
        }


        @Override
        public TypeMultiProperty build() {
            return new TypeMultiProperty(name, description, defaultValues, legalPackageNames, uiOrder, isDefinedInXML);
        }
    }
}
