package net.sourceforge.pmd.lang.rule.properties;

/**
 * Maintains a pair of boundary limit values between which all values managed
 * by the subclasses must fit.
 * 
 * @author Brian Remedios
 */
public abstract class AbstractNumericProperty extends AbstractScalarProperty {

	private Number lowerLimit;
	private Number upperLimit;
	
	/**
	 * 
	 * @param theName
	 * @param theDescription
	 * @param lower
	 * @param upper
	 * @param theDefault
	 * @param theUIOrder
	 * @throws IllegalArgumentException
	 */
	protected AbstractNumericProperty(String theName, String theDescription, Number lower, Number upper, Object theDefault, float theUIOrder) {
		super(theName, theDescription, theDefault, theUIOrder);
	
		if (lower.doubleValue() > upper.doubleValue()) {
			throw new IllegalArgumentException("Lower limit cannot be greater than the upper limit");
		}
		
		lowerLimit = lower;
		upperLimit = upper;
	}
	
	/**
	 * Returns the minimum value that instances of the property can have
	 * @return The minimum value.
	 */
	public Number lowerLimit() {
		return lowerLimit;
	}
	
	/**
	 * Returns the maximum value that instances of the property can have
	 * @return The maximum value.
	 */
	public Number upperLimit() {
		return upperLimit;
	}
	
	/**
	 * Method rangeString.
	 * @return String
	 */
	public String rangeString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(').append(lowerLimit);
		sb.append(" -> ").append(upperLimit);
		sb.append(')');
		return sb.toString();
	}
	
	/**
	 * Returns a string describing any error the value may have when
	 * characterized by the receiver.
	 * 
	 * @param value Object
	 * @return String
	 */
	protected String valueErrorFor(Object value) {
		
		double number = ((Number)value).doubleValue();
		
		if (number > upperLimit.doubleValue() || number < lowerLimit.doubleValue() ) {
			return value + " is out of range " + rangeString();
		}
		
		return null;
	}
}
