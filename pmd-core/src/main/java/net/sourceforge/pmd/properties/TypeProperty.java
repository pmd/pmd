/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Collections;

import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper;
import net.sourceforge.pmd.properties.builders.SinglePackagedPropertyBuilder;
import net.sourceforge.pmd.properties.modules.TypePropertyModule;


/**
 * Defines a property that supports single class types, even for primitive values!
 *
 * TODO - untested for array types
 *
 * @author Brian Remedios
 * @deprecated Will be removed with 7.0.0 with no scheduled replacement yet
 */
@Deprecated
public final class TypeProperty extends AbstractPackagedProperty<Class> {

    /**
     * Constructor for TypeProperty using a string as default value.
     *
     * @param theName           String
     * @param theDescription    String
     * @param defaultTypeStr    String
     * @param legalPackageNames String[]
     * @param theUIOrder        float
     *
     * @throws IllegalArgumentException if the default string could not be parsed into a Class
     * @deprecated will be removed in 7.0.0
     */
    public TypeProperty(String theName, String theDescription, String defaultTypeStr, String[] legalPackageNames,
                        float theUIOrder) {
        this(theName, theDescription, ValueParserConstants.CLASS_PARSER.valueOf(defaultTypeStr), legalPackageNames, theUIOrder, false);
    }


    /** Master constructor. */
    private TypeProperty(String theName, String theDescription, Class<?> theDefault, String[] legalPackageNames,
                         float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, theDefault, theUIOrder, isDefinedExternally,
            new TypePropertyModule(legalPackageNames, Collections.<Class>singletonList(theDefault)));
    }


    /**
     * Constructor for TypeProperty.
     *
     * @param theName           String
     * @param theDescription    String
     * @param theDefault        Class
     * @param legalPackageNames String[]
     * @param theUIOrder        float
     */
    public TypeProperty(String theName, String theDescription, Class<?> theDefault, String[] legalPackageNames,
                        float theUIOrder) {
        this(theName, theDescription, theDefault, legalPackageNames, theUIOrder, false);
    }


    @Override
    public Class<Class> type() {
        return Class.class;
    }


    @Override
    protected String asString(Class value) {
        return value == null ? "" : value.getName();
    }


    @Override
    public Class<?> createFrom(String valueString) {
        return ValueParserConstants.CLASS_PARSER.valueOf(valueString);
    }


    static PropertyDescriptorBuilderConversionWrapper.SingleValue.Packaged<Class, TypePBuilder> extractor() {
        return new PropertyDescriptorBuilderConversionWrapper.SingleValue.Packaged<Class, TypePBuilder>(Class.class, ValueParserConstants.CLASS_PARSER) {
            @Override
            protected TypePBuilder newBuilder(String name) {
                return new TypePBuilder(name);
            }
        };
    }


    public static TypePBuilder named(String name) {
        return new TypePBuilder(name);
    }


    public static final class TypePBuilder extends SinglePackagedPropertyBuilder<Class, TypePBuilder> {
        private TypePBuilder(String name) {
            super(name);
        }


        @Override
        public TypeProperty build() {
            return new TypeProperty(name, description, defaultValue, legalPackageNames, uiOrder, isDefinedInXML);
        }
    }

}
