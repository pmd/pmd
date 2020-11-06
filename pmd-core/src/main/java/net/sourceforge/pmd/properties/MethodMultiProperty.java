/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.properties.ValueParserConstants.METHOD_PARSER;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.properties.builders.MultiPackagedPropertyBuilder;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper;
import net.sourceforge.pmd.properties.modules.MethodPropertyModule;


/**
 * Defines a property type that can specify multiple methods to use as part of a rule.
 *
 * Rule developers can limit the rules to those within designated packages per the 'legalPackages' argument in the
 * constructor which can be an array of partial package names, i.e., ["java.lang", "com.mycompany" ].
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 * @deprecated Will be removed with 7.0.0 with no scheduled replacement
 */
@Deprecated
public final class MethodMultiProperty extends AbstractMultiPackagedProperty<Method> {


    /**
     * Constructor for MethodMultiProperty using an array of defaults.
     *
     * @param theName           String
     * @param theDescription    String
     * @param theDefaults       Method[]
     * @param legalPackageNames String[]
     * @param theUIOrder        float
     */
    public MethodMultiProperty(String theName, String theDescription, Method[] theDefaults,
                               String[] legalPackageNames, float theUIOrder) {
        this(theName, theDescription, Arrays.asList(theDefaults), legalPackageNames, theUIOrder);
    }


    /**
     * Constructor for MethodProperty using a list of defaults.
     *
     * @param theName           String
     * @param theDescription    String
     * @param theDefaults       Method[]
     * @param legalPackageNames String[]
     * @param theUIOrder        float
     *
     * @throws IllegalArgumentException
     */
    public MethodMultiProperty(String theName, String theDescription, List<Method> theDefaults,
                               String[] legalPackageNames, float theUIOrder) {
        this(theName, theDescription, theDefaults, legalPackageNames, theUIOrder, false);
    }


    /** Master constructor. */
    private MethodMultiProperty(String theName, String theDescription, List<Method> theDefaults,
                                String[] legalPackageNames, float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, theDefaults, theUIOrder, isDefinedExternally,
            new MethodPropertyModule(legalPackageNames, theDefaults));
    }


    /**
     * Constructor for MethodProperty.
     *
     * @param theName           String
     * @param theDescription    String
     * @param methodDefaults    String
     * @param legalPackageNames String[]
     * @param theUIOrder        float
     *
     * @throws IllegalArgumentException
     * @deprecated will be removed in 7.O.O
     */
    public MethodMultiProperty(String theName, String theDescription, String methodDefaults,
                               String[] legalPackageNames, float theUIOrder) {
        this(theName, theDescription,
            methodsFrom(methodDefaults),
            legalPackageNames, theUIOrder,
            false);
    }


    @Override
    public String asString(Method value) {
        return MethodPropertyModule.asString(value);
    }


    @Override
    protected Method createFrom(String toParse) {
        return METHOD_PARSER.valueOf(toParse);
    }


    @Override
    public Class<Method> type() {
        return Method.class;
    }


    @Override
    public List<Method> valueFrom(String valueString) throws IllegalArgumentException {
        return methodsFrom(valueString);
    }


    private static List<Method> methodsFrom(String valueString) {
        return ValueParserConstants.parsePrimitives(valueString, MULTI_VALUE_DELIMITER, METHOD_PARSER);
    }


    static PropertyDescriptorBuilderConversionWrapper.MultiValue.Packaged<Method, MethodMultiPBuilder> extractor() {
        return new PropertyDescriptorBuilderConversionWrapper.MultiValue.Packaged<Method, MethodMultiPBuilder>(Method.class, ValueParserConstants.METHOD_PARSER) {
            @Override
            protected MethodMultiPBuilder newBuilder(String name) {
                return new MethodMultiPBuilder(name);
            }
        };
    }


    public static MethodMultiPBuilder named(String name) {
        return new MethodMultiPBuilder(name);
    }


    public static final class MethodMultiPBuilder extends MultiPackagedPropertyBuilder<Method, MethodMultiPBuilder> {
        private MethodMultiPBuilder(String name) {
            super(name);
        }


        @Override
        public MethodMultiProperty build() {
            return new MethodMultiProperty(name, description, defaultValues, legalPackageNames, uiOrder, isDefinedInXML);
        }
    }

}
