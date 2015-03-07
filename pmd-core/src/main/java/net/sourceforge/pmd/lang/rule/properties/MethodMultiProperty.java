/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.lang.reflect.Method;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
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
 */
public class MethodMultiProperty extends AbstractMultiPackagedProperty<Method[]> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<MethodMultiProperty>(
            Method[].class, PACKAGED_FIELD_TYPES_BY_KEY) {

        public MethodMultiProperty createWith(Map<String, String> valuesById) {
            char delimiter = delimiterIn(valuesById);
            return new MethodMultiProperty(nameIn(valuesById), descriptionIn(valuesById), defaultValueIn(valuesById),
                    legalPackageNamesIn(valuesById, delimiter), 0f);
        }
    };

    /**
     * Constructor for MethodProperty.
     *
     * @param theName String
     * @param theDescription String
     * @param theDefaults Method[]
     * @param legalPackageNames String[]
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public MethodMultiProperty(String theName, String theDescription, Method[] theDefaults, String[] legalPackageNames,
            float theUIOrder) {
        super(theName, theDescription, theDefaults, legalPackageNames, theUIOrder);
    }

    /**
     * Constructor for MethodProperty.
     *
     * @param theName String
     * @param theDescription String
     * @param methodDefaults String
     * @param legalPackageNames String[]
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public MethodMultiProperty(String theName, String theDescription, String methodDefaults,
            String[] legalPackageNames, float theUIOrder) {
        super(theName, theDescription, methodsFrom(methodDefaults), legalPackageNames, theUIOrder);
    }

    /**
     * Constructor for MethodProperty.
     *
     * @param theName String
     * @param theDescription String
     * @param methodDefaults String
     * @param otherParams Map<String, String>
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public MethodMultiProperty(String theName, String theDescription, String methodDefaults,
            Map<String, String> otherParams, float theUIOrder) {
        this(theName, theDescription, methodsFrom(methodDefaults), packageNamesIn(otherParams), theUIOrder);
    }

    /**
     * @param methodsStr String
     * @return Method[]
     */
    public static Method[] methodsFrom(String methodsStr) {

        String[] values = StringUtil.substringsOf(methodsStr, DELIMITER);

        Method[] methods = new Method[values.length];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = MethodProperty.methodFrom(values[i], MethodProperty.CLASS_METHOD_DELIMITER,
                    MethodProperty.METHOD_ARG_DELIMITER);
        }
        return methods;
    }

    /**
     * Return the value as a string that can be easily recognized and parsed
     * when we see it again.
     *
     * @param value Object
     * @return String
     */
    @Override
    protected String asString(Object value) {
        return value == null ? "" : MethodProperty.asStringFor((Method) value);
    }

    /**
     * @param item Object
     * @return String
     */
    @Override
    protected String packageNameOf(Object item) {

        final Method method = (Method) item;
        return method.getDeclaringClass().getName() + '.' + method.getName();
    }

    /**
     * @return String
     */
    @Override
    protected String itemTypeName() {
        return "method";
    }

    /**
     *
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Method[]> type() {
        return Method[].class;
    }

    /**
     * @param valueString String
     * @return Object
     * @throws IllegalArgumentException
     * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
     */
    public Method[] valueFrom(String valueString) throws IllegalArgumentException {
        return methodsFrom(valueString);
    }
}
