/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.util.StringUtil;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractProperty<T> implements PropertyDescriptor<T> {

	private final String	name;
	private final String	description;
	private final T 	defaultValue;
	private final boolean 	isRequired;
	private final float		uiOrder;
	
	protected char	multiValueDelimiter = '|';
	
	/**
	 * Constructor for AbstractPMDProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefault Object
	 * @param theUIOrder float
	 * @throws IllegalArgumentException
	 */
	protected AbstractProperty(String theName, String theDescription, T theDefault, float theUIOrder) {
		name = checkNotEmpty(theName, "name");
		description = checkNotEmpty(theDescription, "description");
		defaultValue = theDefault;
		isRequired = false;	// TODO - do we need this?
		uiOrder = checkPositive(theUIOrder, "UI order");
	}
	
	/**
	 * @param arg String
	 * @param argId String
	 * @return String
	 * @throws IllegalArgumentException
	 */
	private static String checkNotEmpty(String arg, String argId) {
		
		if (StringUtil.isEmpty(arg)) {
			throw new IllegalArgumentException("Property attribute '" + argId + "' cannot be null or blank");
		}
		
		return arg;
	}

	/**
	 * @param arg float
	 * @param argId String
	 * @return float
	 * @throws IllegalArgumentException
	 */
	private static float checkPositive(float arg, String argId) {
		if (arg < 0) {
			throw new IllegalArgumentException("Property attribute " + argId + "' must be zero or positive");
		}
		return arg;
	}
	
	/**
	 * Method multiValueDelimiter.
	 * @param aDelimiter char
	 */
	protected void multiValueDelimiter(char aDelimiter) {
		multiValueDelimiter = aDelimiter;
	}
	
	/**
	 * Method multiValueDelimiter.
	 * @return char
	 * @see net.sourceforge.pmd.PropertyDescriptor#multiValueDelimiter()
	 */
	public char multiValueDelimiter() {
		return multiValueDelimiter;
	}
	
	/**
	 * Method name.
	 * @return String
	 * @see net.sourceforge.pmd.PropertyDescriptor#name()
	 */
	public String name() {
		return name;
	}

	/**
	 * Method description.
	 * @return String
	 * @see net.sourceforge.pmd.PropertyDescriptor#description()
	 */
	public String description() {
		return description;
	}

	/**
	 * 
	 * @return Object
	 * @see net.sourceforge.pmd.PropertyDescriptor#defaultValue()
	 */
	public T defaultValue() {
		return defaultValue;
	}
	
	/**
	 * Method defaultHasNullValue.
	 * @return boolean
	 */
	protected boolean defaultHasNullValue() {
		
		if (defaultValue == null) {
			return true;
		}
		
		if (isMultiValue() && isArray(defaultValue)) {
			Object[] defaults = (Object[])defaultValue;
			for (int i=0; i<defaults.length; i++) {
				if (defaults[i] == null) { return true; }
			}
		} 
		
		return false;
	}
	
	/**
	 * Return false, override in appropriate subclasses as necessary.
	 * 
	 * @return boolean
	 * @see net.sourceforge.pmd.PropertyDescriptor#isMultiValue()
	 */
	public boolean isMultiValue() {
		return false;
	}

	/**
	 * Method isRequired.
	 * @return boolean
	 * @see net.sourceforge.pmd.PropertyDescriptor#isRequired()
	 */
	public boolean isRequired() {
		return isRequired;
	}
	
	/**
	 * Method uiOrder.
	 * @return float
	 * @see net.sourceforge.pmd.PropertyDescriptor#uiOrder()
	 */
	public float uiOrder() {
		return uiOrder;
	}
	
	/**
	 * Return the value as a string that can be easily recognized and parsed
	 * when we see it again.
	 * 
	 * @param value Object
	 * @return String
	 */
	protected String asString(Object value) {
		return value == null ? "" : value.toString();
	}
	
	
	/**
	 * Method asDelimitedString.
	 * @param values Object
	 * @return String
	 * @see net.sourceforge.pmd.PropertyDescriptor#asDelimitedString(Object)
	 */
	public String asDelimitedString(T values) {
		
		if (values == null) {
		    return "";
		}
		
		if (values instanceof Object[]) {
			Object[] valueSet = (Object[])values;
			if (valueSet.length == 0) {
			    return "";
			}
			if (valueSet.length == 1) {
			    return asString(valueSet[0]);
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append(asString(valueSet[0]));
			for (int i=1; i<valueSet.length; i++) {
				sb.append(multiValueDelimiter);
				sb.append(asString(valueSet[i]));
			}
			return sb.toString();
			}

		return asString(values);
	}
	
	/**
	 * Method compareTo.
	 * @param otherProperty Object
	 * @return int
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(PropertyDescriptor<?> otherProperty) {
		float otherOrder = otherProperty.uiOrder();
		return (int) (otherOrder - uiOrder);
	}
	
	/**
	 * Method errorFor.
	 * @param value Object
	 * @return String
	 * @see net.sourceforge.pmd.PropertyDescriptor#errorFor(Object)
	 */
	public String errorFor(Object value) {
		
		String typeError = typeErrorFor(value);
		if (typeError != null) {
		    return typeError;
		}
		return isMultiValue() ?
			valuesErrorFor(value) :
			valueErrorFor(value);
	}
	
	/**
	 * Method valueErrorFor.
	 * @param value Object
	 * @return String
	 */
	protected String valueErrorFor(Object value) {
		
		if (value == null) {
			if (defaultHasNullValue()) {
				return null;
			} else {
				return "missing value";
				}
		}
		return null;
	}
	
	/**
	 * Method valuesErrorFor.
	 * @param value Object
	 * @return String
	 */
	protected String valuesErrorFor(Object value) {
		
		if (!isArray(value)) {
			return "multiple values expected";
		}
		
		Object[] values = (Object[])value;
		
		String err = null;
		for (int i=0; i<values.length; i++) {
			err = valueErrorFor(values[i]);
			if (err != null) { return err; }
		}
		
		return null;
	}
	
	/**
	 * Method isArray.
	 * @param value Object
	 * @return boolean
	 */
	protected static boolean isArray(Object value) {
		return value != null && value.getClass().getComponentType() != null;
	}
	
	/**
	 * Method typeErrorFor.
	 * @param value Object
	 * @return String
	 */
	protected String typeErrorFor(Object value) {
		
		if (value == null && !isRequired) {
		    return null;
		}
		
		if (isMultiValue()) {
			if (!isArray(value)) {
				return "Value is not an array of type: " + type();
			}
			
			Class<?> arrayType = value.getClass().getComponentType();
			if (arrayType == null || !arrayType.isAssignableFrom(type().getComponentType())) {
				return "Value is not an array of type: " + type();
			}
			return null;
		}
		
		if (!type().isAssignableFrom(value.getClass())) {
			return value + " is not an instance of " + type();
		}

		return null;
	}
	
	/**
	 * Method propertyErrorFor.
	 * @param rule Rule
	 * @return String
	 * @see net.sourceforge.pmd.PropertyDescriptor#propertyErrorFor(Rule)
	 */
	public String propertyErrorFor(Rule rule) {
	    Object realValue = rule.getProperty(this);
		if (realValue == null && !isRequired()) {
		    return null;
		}
		return errorFor(realValue);
	}
	
	/**
	 * Method choices.
	 * @return Object[][]
	 * @see net.sourceforge.pmd.PropertyDescriptor#choices()
	 */
	public Object[][] choices() {
		return null;
	}
	
	/**
	 * Method preferredRowCount.
	 * @return int
	 * @see net.sourceforge.pmd.PropertyDescriptor#preferredRowCount()
	 */
	public int preferredRowCount() {
		return 1;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) {
		return true;
	    }
	    if (obj == null) {
		return false;
	    }
	    if (obj instanceof PropertyDescriptor) {
		return name.equals(((PropertyDescriptor<?>)obj).name());
	    }
	    return false;
	}

	@Override
	public int hashCode() {
	    return name.hashCode();
	}

	@Override
	public String toString() {
	    return "[PropertyDescriptor: name=" + name() + ", type=" + type() + ", defaultValue=" + defaultValue() + "]";
	}
	
	/**
	 * Method areEqual.
	 * @param value Object
	 * @param otherValue Object
	 * @return boolean
	 */
	@SuppressWarnings("PMD.CompareObjectsWithEquals")
	public static final boolean areEqual(Object value, Object otherValue) {
		if (value == otherValue) {
		    return true;
		}
		if (value == null) {
		    return false;
		}
		if (otherValue == null) {
		    return false;
		}

		return value.equals(otherValue);
	}
}
