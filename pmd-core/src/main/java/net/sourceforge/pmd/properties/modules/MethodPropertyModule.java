/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.modules;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.util.ClassUtil;


/**
 * Factorises common functionality for method properties.
 *
 * @author Clément Fournier
 */
@Deprecated
public class MethodPropertyModule extends PackagedPropertyModule<Method> {

    public static final char CLASS_METHOD_DELIMITER = '#';
    public static final char METHOD_ARG_DELIMITER = ',';
    public static final char[] METHOD_GROUP_DELIMITERS = {'(', ')'};
    public static final String ARRAY_FLAG = "[]";
    private static final Map<Class<?>, String> TYPE_SHORTCUTS = ClassUtil.getClassShortNames();


    public MethodPropertyModule(String[] legalPackageNames, List<Method> defaults) {
        super(legalPackageNames, defaults);
    }


    @Override
    protected String packageNameOf(Method method) {
        return method.getDeclaringClass().getName() + '.' + method.getName();
    }


    @Override
    protected String itemTypeName() {
        return "method";
    }


    public static String asString(Method method) {
        return method == null ? "" : asStringFor(method);
    }


    /**
     * Return the value of `method' as a string that can be easily recognized and parsed when we see it again.
     *
     * @param method the method to convert
     *
     * @return the string value
     */
    private static String asStringFor(Method method) {
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
    private static void asStringOn(Method method, StringBuilder sb) {

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


}
