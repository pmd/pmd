/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.ClassUtil;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a property that supports class types, even for primitive values!
 *
 * @author Brian Remedios
 */
public class TypeProperty extends AbstractPackagedProperty {

    private static final char DELIMITER = '|';

    /**
     * Constructor for TypeProperty.
     * @param theName String
     * @param theDescription String
     * @param theDefault Class
     * @param legalPackageNames String[]
     * @param theUIOrder float
     */
    public TypeProperty(String theName, String theDescription, Class<?> theDefault, String[] legalPackageNames,
            float theUIOrder) {
        super(theName, theDescription, theDefault, legalPackageNames, theUIOrder);

        isMultiValue(false);
    }

    /**
     * Constructor for TypeProperty.
     * @param theName String
     * @param theDescription String
     * @param theDefaults Class[]
     * @param legalPackageNames String[]
     * @param theUIOrder float
     */
    public TypeProperty(String theName, String theDescription, Class<?>[] theDefaults, String[] legalPackageNames,
            float theUIOrder) {
        super(theName, theDescription, theDefaults, legalPackageNames, theUIOrder);

        isMultiValue(true);
    }

    @Override
    protected String packageNameOf(Object item) {
        return ((Class) item).getName();
    }

    /**
     * Method type.
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<?> type() {
        return Class.class;
    }

    @Override
    protected String itemTypeName() {
        return "type";
    }

    /**
     * Method asString.
     * @param value Object
     * @return String
     */
    @Override
    protected String asString(Object value) {
        return value == null ? "" : ((Class<?>) value).getName();
    }

    /**
     * Method classFrom.
     * @param className String
     * @return Class
     * @throws IllegalArgumentException
     */
    private Class<?> classFrom(String className) {

        Class<?> cls = ClassUtil.getTypeFor(className);
        if (cls != null) {
            return cls;
        }

        try {
            return Class.forName(className);
        } catch (Exception ex) {
            throw new IllegalArgumentException(className);
        }
    }

    /**
     * Method valueFrom.
     * @param valueString String
     * @return Object
     * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
     */
    public Object valueFrom(String valueString) {

        if (!isMultiValue()) {
            return classFrom(valueString);
        }

        String[] values = StringUtil.substringsOf(valueString, DELIMITER);

        Class<?>[] classes = new Class<?>[values.length];
        for (int i = 0; i < values.length; i++) {
            classes[i] = classFrom(values[i]);
        }
        return classes;
    }
}
