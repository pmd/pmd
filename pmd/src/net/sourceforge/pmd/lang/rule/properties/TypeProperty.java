/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.ClassUtil;

/**
 * Defines a property that supports class types, even for primitive values!
 * 
 * @author Brian Remedios
 */
public class TypeProperty extends StringProperty {

    private static final char DELIMITER = '|';

    /**
     * Constructor for TypeProperty.
     * @param theName String
     * @param theDescription String
     * @param theDefault Class
     * @param theUIOrder float
     */
    public TypeProperty(String theName, String theDescription, Class<?> theDefault, float theUIOrder) {
    	super(theName, theDescription, theDefault, theUIOrder, DELIMITER);

		isMultiValue(false);
    }

    /**
     * Constructor for TypeProperty.
     * @param theName String
     * @param theDescription String
     * @param theDefaults Class[]
     * @param theUIOrder float
     */
    public TypeProperty(String theName, String theDescription, Class<?>[] theDefaults, float theUIOrder) {
    	super(theName, theDescription, theDefaults, theUIOrder, DELIMITER);

		isMultiValue(true);
    }

    /**
     * Method type.
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    @Override
    public Class<?> type() {
    	return Class.class;
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
    @Override
    public Object valueFrom(String valueString) {

		if (!isMultiValue()) {
		    return classFrom(valueString);
		}
	
		String[] values = (String[]) super.valueFrom(valueString);
	
		Class<?>[] classes = new Class[values.length];
		for (int i = 0; i < values.length; i++) {
		    classes[i] = classFrom(values[i]);
		}
		return classes;
    }

    /**
     * Neutralize unwanted superclass functionality that will result 
     * in a class cast exception.
     * 
     * @param value Object
     * @return String
     */
    @Override
    protected String valueErrorFor(Object value) {
    	return null;
    }
}
