/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.util.ClassUtil;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a property type that can specify a single method to use as part of a
 * rule.
 *
 * <p>Rule developers can limit the rules to those within designated packages per
 * the 'legalPackages' argument in the constructor which can be an array of
 * partial package names, i.e., ["java.lang", "com.mycompany" ].</p>
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class MethodProperty extends AbstractPackagedProperty<Method> {


    public static final char CLASS_METHOD_DELIMITER = '#';
    public static final char METHOD_ARG_DELIMITER = ',';
    public static final char[] METHOD_GROUP_DELIMITERS = {'(', ')'};
    public static final PropertyDescriptorFactory<Method> FACTORY // @formatter:off
        = new SingleValuePropertyDescriptorFactory<Method>(Method.class, PACKAGED_FIELD_TYPES_BY_KEY) {
            @Override
            public MethodProperty createWith(Map<PropertyDescriptorField, String> valuesById, boolean isDefinedExternally) {
                char delimiter = delimiterIn(valuesById);
                return new MethodProperty(nameIn(valuesById),
                                          descriptionIn(valuesById),
                                          methodFrom(defaultValueIn(valuesById)),
                                          legalPackageNamesIn(valuesById, delimiter),
                                          0f,
                                          isDefinedExternally);
            }
        }; // @formatter:on
    private static final String ARRAY_FLAG = "[]";
    private static final Map<Class<?>, String> TYPE_SHORTCUTS = ClassUtil.getClassShortNames();


    public MethodProperty(String theName, String theDescription, Method theDefault, String[] legalPackageNames,
                          float theUIOrder) {
        this(theName, theDescription, theDefault, legalPackageNames, theUIOrder, false);
    }


    /**
     * Constructor for MethodProperty using a string as a default value.
     *
     * @param theName           Name of the property
     * @param theDescription    Description
     * @param defaultMethodStr  Default value, that will be parsed into a Method object
     * @param legalPackageNames Legal packages
     * @param theUIOrder        UI order
     */
    public MethodProperty(String theName, String theDescription, String defaultMethodStr, String[] legalPackageNames,
                          float theUIOrder) {
        this(theName, theDescription, methodFrom(defaultMethodStr), legalPackageNames, theUIOrder, false);
    }

    /**
     * Constructor for MethodProperty.
     *
     * @param theName           Name of the property
     * @param theDescription    Description
     * @param theDefault        Default value
     * @param legalPackageNames Legal packages
     * @param theUIOrder        UI order
     */
    private MethodProperty(String theName, String theDescription, Method theDefault, String[] legalPackageNames,
                            float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, theDefault, legalPackageNames, theUIOrder, isDefinedExternally);
    }

    @Override
    protected String asString(Method value) {
        return value == null ? "" : asStringFor(value);
    }


    /**
     * Return the value of `method' as a string that can be easily recognized
     * and parsed when we see it again.
     *
     * @param method the method to convert
     *
     * @return the string value
     */
    public static String asStringFor(Method method) {
        StringBuilder sb = new StringBuilder();
        asStringOn(method, sb);
        return sb.toString();
    }


    /**
     * Serializes the method signature onto the specified buffer.
     *
     * @param method Method
     * @param sb     StringBuilder
     */
    public static void asStringOn(Method method, StringBuilder sb) {

        Class<?> clazz = method.getDeclaringClass();

        sb.append(shortestNameFor(clazz));
        sb.append(CLASS_METHOD_DELIMITER);
        sb.append(method.getName());

        sb.append(METHOD_GROUP_DELIMITERS[0]);

        Class<?>[] argTypes = method.getParameterTypes();
        if (argTypes.length == 0) {
            sb.append(METHOD_GROUP_DELIMITERS[1]);
            return;
        }

        serializedTypeIdOn(argTypes[0], sb);
        for (int i = 1; i < argTypes.length; i++) {
            sb.append(METHOD_ARG_DELIMITER);
            serializedTypeIdOn(argTypes[i], sb);
        }
        sb.append(METHOD_GROUP_DELIMITERS[1]);
    }


    private static String shortestNameFor(Class<?> cls) {
        String compactName = TYPE_SHORTCUTS.get(cls);
        return compactName == null ? cls.getName() : compactName;
    }


    private static void serializedTypeIdOn(Class<?> type, StringBuilder sb) {

        Class<?> arrayType = type.getComponentType();
        if (arrayType == null) {
            sb.append(shortestNameFor(type));
            return;
        }
        sb.append(shortestNameFor(arrayType)).append(ARRAY_FLAG);
    }


    @Override
    protected String packageNameOf(Method method) {
        return method == null ? null : method.getDeclaringClass().getName() + '.' + method.getName();
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
    public Method createFrom(String valueString) throws IllegalArgumentException {
        return methodFrom(valueString);
    }


    private static Class<?> typeFor(String typeName) {

        Class<?> type;

        if (typeName.endsWith(ARRAY_FLAG)) {
            String arrayTypeName = typeName.substring(0, typeName.length() - ARRAY_FLAG.length());
            type = typeFor(arrayTypeName); // recurse
            return Array.newInstance(type, 0).getClass(); // TODO is there a
            // better way to get
            // an array type?
        }

        type = ClassUtil.getTypeFor(typeName); // try shortcut first
        if (type != null) {
            return type;
        }

        try {
            return Class.forName(typeName);
        } catch (Exception ex) {
            return null;
        }
    }


    /**
     * Returns the method specified within the string argument after parsing out
     * its source class and any optional arguments. Callers need to specify the
     * delimiters expected between the various elements. I.e.:
     *
     * <p>"String#isEmpty()" "String#indexOf(int)" "String#substring(int,int)"
     *
     * <p>If a method isn't part of the specified class we will walk up any
     * superclasses to Object to try and find it.
     *
     * <p>If the classes are listed in the ClassUtil class within in Typemaps then
     * you likely can avoid specifying fully-qualified class names per the above
     * example.
     *
     * <p>Returns null if a matching method cannot be found.
     *
     * @param methodNameAndArgTypes Method name (with its declaring class and arguments)
     * @param classMethodDelimiter  Delimiter between the class and method names
     * @param methodArgDelimiter    Method arguments delimiter
     *
     * @return Method
     */
    public static Method methodFrom(String methodNameAndArgTypes, char classMethodDelimiter, char methodArgDelimiter) {

        // classname#methodname(arg1,arg2)
        // 0 1 2

        int delimPos0 = -1;
        if (methodNameAndArgTypes != null) {
            delimPos0 = methodNameAndArgTypes.indexOf(classMethodDelimiter);
        } else {
            return null;
        }

        if (delimPos0 < 0) {
            return null;
        }

        String className = methodNameAndArgTypes.substring(0, delimPos0);
        Class<?> type = ClassUtil.getTypeFor(className);
        if (type == null) {
            return null;
        }

        int delimPos1 = methodNameAndArgTypes.indexOf(METHOD_GROUP_DELIMITERS[0]);
        if (delimPos1 < 0) {
            String methodName = methodNameAndArgTypes.substring(delimPos0 + 1);
            return ClassUtil.methodFor(type, methodName, ClassUtil.EMPTY_CLASS_ARRAY);
        }

        String methodName = methodNameAndArgTypes.substring(delimPos0 + 1, delimPos1);
        if (StringUtil.isEmpty(methodName)) {
            return null;
        } // missing method name?

        int delimPos2 = methodNameAndArgTypes.indexOf(METHOD_GROUP_DELIMITERS[1]);
        if (delimPos2 < 0) {
            return null;
        } // error!

        String argTypesStr = methodNameAndArgTypes.substring(delimPos1 + 1, delimPos2);
        if (StringUtil.isEmpty(argTypesStr)) {
            return ClassUtil.methodFor(type, methodName, ClassUtil.EMPTY_CLASS_ARRAY);
        } // no arg(s)

        String[] argTypeNames = StringUtil.substringsOf(argTypesStr, methodArgDelimiter);
        Class<?>[] argTypes = new Class[argTypeNames.length];
        for (int i = 0; i < argTypes.length; i++) {
            argTypes[i] = typeFor(argTypeNames[i]);
        }

        return ClassUtil.methodFor(type, methodName, argTypes);
    }


    /**
     * Parses a String into a Method.
     *
     * @param methodStr String to parse
     *
     * @return Parsed Method
     */
    public static Method methodFrom(String methodStr) {
        return methodFrom(methodStr, CLASS_METHOD_DELIMITER, METHOD_ARG_DELIMITER);
    }
}
