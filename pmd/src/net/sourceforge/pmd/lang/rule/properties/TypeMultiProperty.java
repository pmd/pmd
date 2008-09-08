/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a property that supports multiple class types, even for primitive values!
 * 
 * TODO - untested for array types
 *
 * @author Brian Remedios
 */
public class TypeMultiProperty extends AbstractPackagedProperty<Class[]> {

    private static final char DELIMITER = '|';

    /**
     * Constructor for TypeProperty.
     * @param theName String
     * @param theDescription String
     * @param theDefaults Class[]
     * @param legalPackageNames String[]
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public TypeMultiProperty(String theName, String theDescription, Class<?>[] theDefaults, String[] legalPackageNames,
            float theUIOrder) {
        super(theName, theDescription, theDefaults, legalPackageNames, theUIOrder);

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
	 * @return boolean
	 * @see net.sourceforge.pmd.PropertyDescriptor#isMultiValue()
	 */
	@Override
	public boolean isMultiValue() {
	    return true;
	}
    
    /**
     * Method type.
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Class[]> type() {
        return Class[].class;
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
     * Method valueFrom.
     * @param valueString String
     * @return Object
     * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
     */
    public Class<?>[] valueFrom(String valueString) {
        String[] values = StringUtil.substringsOf(valueString, DELIMITER);

        Class<?>[] classes = new Class<?>[values.length];
        for (int i = 0; i < values.length; i++) {
            classes[i] = TypeProperty.classFrom(values[i]);
        }
        return classes;
    }
}
