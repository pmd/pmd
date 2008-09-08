/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.ClassUtil;

/**
 * Defines a property that supports single class types, even for primitive values!
 * 
 * TODO - untested for array types
 *
 * @author Brian Remedios
 */
public class TypeProperty extends AbstractPackagedProperty<Class> {

    private static final char DELIMITER = '|';

    /**
     * Constructor for TypeProperty.
     * @param theName String
     * @param theDescription String
     * @param theDefault Class
     * @param legalPackageNames String[]
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public TypeProperty(String theName, String theDescription, Class<?> theDefault, String[] legalPackageNames, float theUIOrder) {
        super(theName, theDescription, theDefault, legalPackageNames, theUIOrder);
    }

    /**
     * Method packageNameOf.
     * @param item Object
     * @return String
     */
    @Override
    protected String packageNameOf(Object item) {
        return ((Class<?>) item).getName();
    }

    /**
     * Method type.
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Class> type() {
        return Class.class;
    }

    /**
     * Method itemTypeName.
     * @return String
     */
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
    static Class<?> classFrom(String className) {

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
    public Class<?> valueFrom(String valueString) {
        return classFrom(valueString);
    }
}
