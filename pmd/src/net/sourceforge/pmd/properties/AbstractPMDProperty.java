package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;


/**
 * 
 * @author Brian Remedios
 * @version $Revision$
 */
public abstract class AbstractPMDProperty implements PropertyDescriptor {

	private String	name;
	private String	description;
	private Object 	defaultValue;
	private boolean isRequired = false;
	private int		maxValueCount = 1;
	private float	uiOrder;
	
	protected char	multiValueDelimiter = '|';
	
	/**
	 * Constructor for AbstractPMDProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theDefault Object
	 * @param theUIOrder float
	 */
	protected AbstractPMDProperty(String theName, String theDescription, Object theDefault, float theUIOrder) {
		name = theName;
		description = theDescription;
		defaultValue = theDefault;
		uiOrder = theUIOrder;
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
	public Object defaultValue() {
		return defaultValue;
	}
	
	/**
	 * Method maxValueCount.
	 * @return int
	 * @see net.sourceforge.pmd.PropertyDescriptor#maxValueCount()
	 */
	public int maxValueCount() {
		return maxValueCount;
	}
	
	/**
	 * Method maxValueCount.
	 * @param theCount int
	 * @see net.sourceforge.pmd.PropertyDescriptor#maxValueCount()
	 */
	protected void maxValueCount(int theCount) {
		maxValueCount = theCount;
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
	public String asDelimitedString(Object values) {
		
		if (values == null) return "";
		
		if (values instanceof Object[]) {
			Object[] valueSet = (Object[])values;
			if (valueSet.length == 0) return "";
			if (valueSet.length == 1) return asString(valueSet[0]);
			
			StringBuffer sb = new StringBuffer();
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
	public int compareTo(Object otherProperty) {
		float otherOrder = ((PropertyDescriptor)otherProperty).uiOrder();
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
		if (typeError != null) return typeError;
		return valueErrorFor(value);
	}
	
	/**
	 * Method valueErrorFor.
	 * @param value Object
	 * @return String
	 */
	protected String valueErrorFor(Object value) {
		// override as required
		return null;
	}
	
	/**
	 * Method isArray.
	 * @param value Object
	 * @return boolean
	 */
	protected boolean isArray(Object value) {
		return value != null && value.getClass().getComponentType() != null;
	}
	
	/**
	 * Method typeErrorFor.
	 * @param value Object
	 * @return String
	 */
	protected String typeErrorFor(Object value) {
		
		if (value == null && !isRequired) return null;
		
		if (maxValueCount > 1) {
			if (!isArray(value)) {
				return "Value is not an array of type: " + type();
			}
			
			Class arrayType = value.getClass().getComponentType();
			if (arrayType == null || !arrayType.isAssignableFrom(type())) {
				return "Value is not an array of type: " + type();
			}
			return null;
		}
		
		if (!type().isAssignableFrom(value.getClass())) {
			return "" + value + " is not an instance of " + type();
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
		String strValue = rule.getStringProperty(name());
		if (strValue == null && !isRequired()) return null;
		Object realValue = valueFrom(strValue);
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
	
	/**
	 * Method areEqual.
	 * @param value Object
	 * @param otherValue Object
	 * @return boolean
	 */
	public static final boolean areEqual(Object value, Object otherValue) {
		if (value == otherValue) return true;
		if (value == null) return false;
		if (otherValue == null) return false;

		return value.equals(otherValue);
	}
}
