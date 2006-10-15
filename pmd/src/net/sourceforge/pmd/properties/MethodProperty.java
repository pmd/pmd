package net.sourceforge.pmd.properties;

import java.lang.reflect.Method;

/**
 * @author Brian Remedios
 */
public class MethodProperty extends AbstractPMDProperty {
	
	/**
	 * Constructor for MethodProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefault Object
	 * @param theUIOrder float
	 */
	public MethodProperty(String theName, String theDescription, Object theDefault, float theUIOrder) {
		super(theName, theDescription, theDefault, theUIOrder);
	}

	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class type() {
		return Method.class;
	}

	/**
	 * Method valueFrom.
	 * @param propertyString String
	 * @return Object
	 * @throws IllegalArgumentException
	 * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
	 */
	public Object valueFrom(String propertyString) throws IllegalArgumentException {
		
		Class cls = classIn(propertyString);
		String methodName = methodNameIn(propertyString);
		Class[] parameterTypes = parameterTypesIn(propertyString);
		
		try {
			return cls.getMethod(methodName, parameterTypes);
		} catch (Exception e) {
			throw new IllegalArgumentException("invalid method: " + propertyString);
		}
	}

	private Class classIn(String propertyString) throws IllegalArgumentException {
		
		int dotPos = propertyString.lastIndexOf('.');
		String className = propertyString.substring(0, dotPos);
		
		try {
			return Class.forName(className);
			} catch (Exception ex) {
				throw new IllegalArgumentException("class not found: " + className);
			}
	}
	
	private String methodNameIn(String propertyString) throws IllegalArgumentException {
		
		int dotPos = propertyString.lastIndexOf('.');
		return propertyString.substring(dotPos);
	}
	
	private Class[] parameterTypesIn(String propertyString) {
		return null;
	}
}
