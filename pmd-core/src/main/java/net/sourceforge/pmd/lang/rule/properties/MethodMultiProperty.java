/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.lang.rule.properties.ValueParsers.METHOD_PARSER;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.modules.MethodPropertyModule;

/**
 * Defines a property type that can specify multiple methods to use as part of a
 * rule.
 *
 * Rule developers can limit the rules to those within designated packages per
 * the 'legalPackages' argument in the constructor which can be an array of
 * partial package names, i.e., ["java.lang", "com.mycompany" ].
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class MethodMultiProperty extends AbstractMultiPackagedProperty<Method> {

    /** Factory. */
    public static final PropertyDescriptorFactory<List<Method>> FACTORY // @formatter:off
        = new MultiValuePropertyDescriptorFactory<Method>(Method.class, PACKAGED_FIELD_TYPES_BY_KEY) {
            @Override
            public MethodMultiProperty createWith(Map<PropertyDescriptorField, String> valuesById, boolean isDefinedExternally) {
                char delimiter = delimiterIn(valuesById, MULTI_VALUE_DELIMITER);
                return new MethodMultiProperty(nameIn(valuesById),
                                               descriptionIn(valuesById),
                                               methodsFrom(defaultValueIn(valuesById)),
                                               legalPackageNamesIn(valuesById, delimiter),
                                               0f,
                                               isDefinedExternally);
            }

        }; // @formatter:on


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


    private static List<Method> methodsFrom(String valueString) {
        return ValueParsers.parsePrimitives(valueString, MULTI_VALUE_DELIMITER, METHOD_PARSER);
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
}
