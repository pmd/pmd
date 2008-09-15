/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.HashMap;
import java.util.Map;

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
	private final T 		defaultValue;
	private final boolean 	isRequired;
	private final float		uiOrder;
	
	private static final char DELIMITER = '|';
	
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
	 *
	 * @return char
	 * @see net.sourceforge.pmd.PropertyDescriptor#multiValueDelimiter()
	 */
	public char multiValueDelimiter() {
		return DELIMITER;
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
	 * @param values T
	 * @return String
	 * @see net.sourceforge.pmd.PropertyDescriptor#asDelimitedString(T)
	 */
	public String asDelimitedString(T values) {
	    return asDelimitedString(values, multiValueDelimiter());
	}
	
	/**
	 * Return the specified values as a single string using the delimiter.
	 * @param values Object
	 * @param delimiter char
	 * @return String
	 * @see net.sourceforge.pmd.PropertyDescriptor#asDelimitedString(Object)
	 */
	public String asDelimitedString(T values, char delimiter) {
		
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
				sb.append(delimiter);
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
	 * @param value Object
	 * @return String
	 */
	protected String valueErrorFor(Object value) {
		
		if (value == null) {
			if (defaultHasNullValue()) {
				return null;
			}
			return "missing value";
		}
		return null;
	}
	
	/**
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
	 * @param value Object
	 * @return boolean
	 */
	protected static boolean isArray(Object value) {
		return value != null && value.getClass().getComponentType() != null;
	}
	
	/**
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
	 * Most property types do not provide a set of value choices, override
	 * as necessary in concrete subclasses.
	 * @return Object[][]
	 * @see net.sourceforge.pmd.PropertyDescriptor#choices()
	 */
	public Object[][] choices() {
		return null;
	}
	
	/**
	 * @return int
	 * @see net.sourceforge.pmd.PropertyDescriptor#preferredRowCount()
	 */
	public int preferredRowCount() {
		return 1;
	}
	
	/**
	 * @param obj Object
	 * @return boolean
	 */
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

	/**
	 * @return int
	 */
	@Override
	public int hashCode() {
	    return name.hashCode();
	}

	/**
	 * Method toString.
	 * @return String
	 */
	@Override
	public String toString() {
	    return "[PropertyDescriptor: name=" + name() + ", type=" + type() + ", defaultValue=" + defaultValue() + "]";
	}
	
	/**
	 * @return String
	 */
	protected abstract String defaultAsString();
	
	/**
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
	
	/**
	 * @return Map<String,String>
	 * @see
	 */
	public Map<String, String> attributeValuesById() {
		
		Map<String, String> values = new HashMap<String, String>();
		addAttributesTo(values);		
		return values;
	}
	
	/**
	 * @param attributes Map<String,String>
	 */
	protected void addAttributesTo(Map<String, String> attributes) {
		attributes.put("description", description);
		attributes.put("default", defaultAsString());
	}
	
}
