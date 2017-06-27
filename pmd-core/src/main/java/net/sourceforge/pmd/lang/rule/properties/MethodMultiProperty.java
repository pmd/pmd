/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.util.StringUtil;

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
    public static final PropertyDescriptorFactory FACTORY // @formatter:off
        = new BasicPropertyDescriptorFactory<List<Method>>(Method.class, PACKAGED_FIELD_TYPES_BY_KEY) {
            @Override
            public MethodMultiProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
                char delimiter = delimiterIn(valuesById);
                return new MethodMultiProperty(nameIn(valuesById), descriptionIn(valuesById), defaultValueIn(valuesById),
                                               legalPackageNamesIn(valuesById, delimiter), 0f);
            }
        }; // @formatter:on


    public MethodMultiProperty(String theName, String theDescription, Method[] theDefaults,
                               String[] legalPackageNames, float theUIOrder) {
        this(theName, theDescription, Arrays.asList(theDefaults), legalPackageNames, theUIOrder);
    }


    /**
     * Constructor for MethodProperty.
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
        super(theName, theDescription, theDefaults, legalPackageNames, theUIOrder);
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
     */
    public MethodMultiProperty(String theName, String theDescription, String methodDefaults,
                               String[] legalPackageNames, float theUIOrder) {
        super(theName, theDescription, methodsFrom(methodDefaults), legalPackageNames, theUIOrder);
    }


    public static List<Method> methodsFrom(String methodsStr) {
        String[] values = StringUtil.substringsOf(methodsStr, DELIMITER);

        List<Method> methods = new ArrayList<>(values.length);
        for (String name : values) {
            methods.add(MethodProperty.methodFrom(name, MethodProperty.CLASS_METHOD_DELIMITER,
                                                  MethodProperty.METHOD_ARG_DELIMITER));
        }
        return methods;
    }


    // TODO:cf deprecate this
    public MethodMultiProperty(String theName, String theDescription, String methodDefaults,
                               Map<PropertyDescriptorField, String> otherParams, float theUIOrder) {
        this(theName, theDescription, methodsFrom(methodDefaults), packageNamesIn(otherParams), theUIOrder);
    }


    @Override
    public String asString(Method value) {
        return value == null ? "" : MethodProperty.asStringFor(value);
    }


    @Override
    protected Method createFrom(String toParse) {
        return MethodProperty.methodFrom(toParse, MethodProperty.CLASS_METHOD_DELIMITER,
                                         MethodProperty.METHOD_ARG_DELIMITER);
    }


    @Override
    protected String packageNameOf(Method item) {
        final Method method = item;
        return method.getDeclaringClass().getName() + '.' + method.getName();
    }


    @Override
    protected String itemTypeName() {
        return "method";
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
