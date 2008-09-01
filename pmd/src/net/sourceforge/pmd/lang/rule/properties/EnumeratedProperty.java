/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

/**
 * Defines a datatype with a set of preset values of any type as held within a pair of
 * maps. While the values are not serialized out, the labels are and serve as keys to
 * obtain the values.  The choices() method provides the ordered selections to be used
 * in an editor widget.
 * 
 * @author Brian Remedios
 */
public class EnumeratedProperty<E> extends AbstractEnumeratedProperty<E, Object> {
	
	/**
	 * Constructor for EnumeratedProperty.
	 * @param theName String
	 * @param theDescription String
     * @param theLabels String[]
   	 * @param theChoices E[]
   	 * @param defaultIndex int
	 * @param theUIOrder float
	 * @throws IllegalArgumentException
	 */
	public EnumeratedProperty(String theName, String theDescription, String[] theLabels, E[] theChoices, int defaultIndex, float theUIOrder) {
		super(theName, theDescription, theLabels, theChoices, new int[] {defaultIndex}, theUIOrder, false);
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Object> type() {
		return Object.class;
	}
	
	/**
	 * Method errorFor.
	 * @param value Object
	 * @return String
	 * @see net.sourceforge.pmd.PropertyDescriptor#errorFor(Object)
	 */
	@Override
	public String errorFor(Object value) {
			return labelsByChoice.containsKey(value) ?
				null : nonLegalValueMsgFor(value);
	}
	
	/**
	 * Method valueFrom.
	 * @param value String
	 * @return Object
	 * @throws IllegalArgumentException
	 * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
	 */
	public Object valueFrom(String value) throws IllegalArgumentException {
		    return choiceFrom(value);
	}
	
	/**
	 * Method asDelimitedString.
	 * @param value Object
	 * @return String
	 * @see net.sourceforge.pmd.PropertyDescriptor#asDelimitedString(Object)
	 */
	@Override
	public String asDelimitedString(Object value) {
		    return labelsByChoice.get(value);
	}
}
