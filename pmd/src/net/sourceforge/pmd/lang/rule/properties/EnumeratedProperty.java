/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a datatype with a set of preset values of any type as held within a pair of
 * maps. While the values are not serialized out, the labels are and serve as keys to
 * obtain the values.  The choices() method provides the ordered selections to be used
 * in an editor widget.
 * 
 * @author Brian Remedios
 */
public class EnumeratedProperty<E> extends AbstractProperty {

	private Map<String, E>	choicesByLabel;
	private Map<E, String>	labelsByChoice;
	
	private String[]		orderedLabels;
	private Object[][] 		choices;
	
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
		this(theName, theDescription, theLabels, theChoices, new int[] {defaultIndex}, theUIOrder);
	}
	
	/**
	 * Constructor for EnumeratedProperty.
	 * @param theName String
	 * @param theDescription String
     * @param theLabels String[]
     * @param theChoices E[]
     * @param choiceIndices int[]
	 * @param theUIOrder float
	 * @throws IllegalArgumentException
	 */
	public EnumeratedProperty(String theName, String theDescription, String[] theLabels, E[] theChoices, int[] choiceIndices, float theUIOrder) {
		super(theName, theDescription, selectionsIn(theLabels, choiceIndices), theUIOrder);

		choicesByLabel = CollectionUtil.mapFrom(theLabels, theChoices);
		labelsByChoice = CollectionUtil.invertedMapFrom(choicesByLabel);
		orderedLabels = theLabels;
		
		isMultiValue(choiceIndices.length > 1);
	}
	
	private static String[] selectionsIn(String[] items, int[] selectionIndices) {
		
		String[] selections = new String[selectionIndices.length];
		final int maxIdx = items.length - 1;
		for (int i=0; i<selections.length; i++) {
			if (i < 0 || i > maxIdx) {
				throw new IllegalArgumentException("Invalid item index: " + i);
			}
			selections[i] = items[selectionIndices[i]];
		}
		return selections;
	}
	
	/**
	 * Method type.
	 * @return Class
	 * @see net.sourceforge.pmd.PropertyDescriptor#type()
	 */
	public Class<Object> type() {
		return Object.class;
	}

	private String nonLegalValueMsgFor(Object value) {
		return value + " is not a legal value";
	}
	
	/**
	 * Method errorFor.
	 * @param value Object
	 * @return String
	 * @see net.sourceforge.pmd.PropertyDescriptor#errorFor(Object)
	 */
	public String errorFor(Object value) {
		
		if (!isMultiValue()) {
			return labelsByChoice.containsKey(value) ?
				null : nonLegalValueMsgFor(value);
		}
		
		Object[] values = (Object[])value;
		for (int i=0; i<values.length; i++) {
			if (labelsByChoice.containsKey(values[i])) {
			    continue;
			}
			return nonLegalValueMsgFor(values[i]);
		}
		return null;
	}
	
	/**
	 * Method choiceFrom.
	 * @param label String
	 * @return E
	 */
	private E choiceFrom(String label) {
		E result = choicesByLabel.get(label);
		if (result != null) {
		    return result;
		}
		throw new IllegalArgumentException(label);
	}
	
	/**
	 * 
	 * @return Object[][]
	 * @see net.sourceforge.pmd.PropertyDescriptor#choices()
	 */
	public Object[][] choices() {
	
		if (choices != null) {
			return choices;
		}
		
		choices = new Object[orderedLabels.length][2];
		
		for (int i=0; i<choices.length; i++) {		
			choices[i][0] = orderedLabels[i];
			choices[i][1] = choicesByLabel.get(orderedLabels[i]);
		}
		orderedLabels = null;	// no longer needed
		return choices;
	}
	
	/**
	 * Method valueFrom.
	 * @param value String
	 * @return Object
	 * @throws IllegalArgumentException
	 * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
	 */
	public Object valueFrom(String value) throws IllegalArgumentException {
		
		if (!isMultiValue()) {
		    return choiceFrom(value);
		}
		
		String[] strValues = StringUtil.substringsOf(value, multiValueDelimiter);
		
		Object[] values = new Object[strValues.length];
		for (int i=0;i<values.length; i++) {
		    values[i] = choiceFrom(strValues[i]);
		}
		return values;
	}
	
	/**
	 * Method asDelimitedString.
	 * @param value Object
	 * @return String
	 * @see net.sourceforge.pmd.PropertyDescriptor#asDelimitedString(Object)
	 */
	public String asDelimitedString(Object value) {
		
		if (!isMultiValue()) {
		    return labelsByChoice.get(value);
		}
		
		Object[] choices = (Object[])value;
		
		StringBuilder sb = new StringBuilder();

		sb.append(labelsByChoice.get(choices[0]));
		
		for (int i=1; i<choices.length; i++) {
			sb.append(multiValueDelimiter);
			sb.append(labelsByChoice.get(choices[i]));
		}

		return sb.toString();
	}
}
