package net.sourceforge.pmd.properties;

import java.util.Map;

import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a datatype with a set of preset values of any type as held within a pair of
 * maps. While the values are not serialized out, the labels are and serve as keys to
 * obtain the values.
 * 
 * @author Brian Remedios
 * @version $Revision$
 */
public class EnumeratedProperty extends AbstractPMDProperty {

	private Object[][]	choiceTuples;
	private Map			choicesByLabel;
	private Map			labelsByChoice;
	
	/**
	 * Constructor for EnumeratedProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theChoices Object[][]
	 * @param theUIOrder float
	 */
	public EnumeratedProperty(String theName, String theDescription, Object[][] theChoices, float theUIOrder) {
		this(theName, theDescription, theChoices, theUIOrder, 1);
	}
	
	/**
	 * Constructor for EnumeratedProperty.
	 * @param theName String
	 * @param theDescription String
	 * @param theChoices Object[][]
	 * @param theUIOrder float
	 * @param maxValues int
	 */
	public EnumeratedProperty(String theName, String theDescription, Object[][] theChoices, float theUIOrder, int maxValues) {
		super(theName, theDescription, theChoices[0][1], theUIOrder);
		
		choiceTuples = theChoices;
		choicesByLabel = CollectionUtil.mapFrom(theChoices);
		labelsByChoice = CollectionUtil.invertedMapFrom(choicesByLabel);
		
		maxValueCount(maxValues);
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class type() {
		return Object.class;
	}

	/**
	 * Method choices.
	 * @return Object[][]
	 * @see net.sourceforge.pmd.PropertyDescriptor#choices()
	 */
	public Object[][] choices() {
		return choiceTuples;
	}
	
	private String nonLegalValueMsgFor(Object value) {
		return "" + value + " is not a legal value";
	}
	
	/**
	 * Method errorFor.
	 * @param value Object
	 * @return String
	 * @see net.sourceforge.pmd.PropertyDescriptor#errorFor(Object)
	 */
	public String errorFor(Object value) {
		
		if (maxValueCount() == 1) {
			return labelsByChoice.containsKey(value) ?
				null : nonLegalValueMsgFor(value);
		}
		
		Object[] values = (Object[])value;
		for (int i=0; i<values.length; i++) {
			if (labelsByChoice.containsKey(values[i])) continue;
			return nonLegalValueMsgFor(values[i]);
		}
		return null;
	}
	
	/**
	 * Method choiceFrom.
	 * @param label String
	 * @return Object
	 */
	private Object choiceFrom(String label) {
		Object result = choicesByLabel.get(label);
		if (result != null) return result;
		throw new IllegalArgumentException(label);
	}
	
	/**
	 * Method valueFrom.
	 * @param value String
	 * @return Object
	 * @throws IllegalArgumentException
	 * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
	 */
	public Object valueFrom(String value) throws IllegalArgumentException {
		
		if (maxValueCount() == 1) return choiceFrom(value);
		
		String[] strValues = StringUtil.substringsOf(value, multiValueDelimiter);
		
		Object[] values = new Object[strValues.length];
		for (int i=0;i<values.length; i++) values[i] = choiceFrom(strValues[i]);
		return values;
	}
	
	/**
	 * Method asDelimitedString.
	 * @param value Object
	 * @return String
	 * @see net.sourceforge.pmd.PropertyDescriptor#asDelimitedString(Object)
	 */
	public String asDelimitedString(Object value) {
		
		if (maxValueCount() == 1) return (String)labelsByChoice.get(value);
		
		Object[] choices = (Object[])value;
		
		StringBuffer sb = new StringBuffer();

		sb.append(labelsByChoice.get(choices[0]));
		
		for (int i=1; i<choices.length; i++) {
			sb.append(multiValueDelimiter);
			sb.append(labelsByChoice.get(choices[i]));
		}

		return sb.toString();
	}
}
