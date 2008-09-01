/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.util.CollectionUtil;

public abstract class AbstractEnumeratedProperty<E, T> extends AbstractProperty<T> {

    protected Map<String, E> choicesByLabel;
    protected Map<E, String> labelsByChoice;

    private String[] orderedLabels;
    protected Object[][] choices;

    @SuppressWarnings("unchecked")
    public AbstractEnumeratedProperty(String theName, String theDescription, String[] theLabels, E[] theChoices,
	    int[] choiceIndices, float theUIOrder, boolean multi) {
	super(theName, theDescription, (T) selectionsIn(theLabels, choiceIndices, multi), theUIOrder);

	choicesByLabel = CollectionUtil.mapFrom(theLabels, theChoices);
	labelsByChoice = CollectionUtil.invertedMapFrom(choicesByLabel);
	orderedLabels = theLabels;

	isMultiValue(multi);
    }

    private static Object selectionsIn(String[] items, int[] selectionIndices, boolean multi) {
	String[] selections = new String[selectionIndices.length];
	final int maxIdx = items.length - 1;
	for (int i = 0; i < selections.length; i++) {
	    if (i < 0 || i > maxIdx) {
		throw new IllegalArgumentException("Invalid item index: " + i);
	    }
	    selections[i] = items[selectionIndices[i]];
	}
	return multi ? selections : selections[0];
    }

    protected String nonLegalValueMsgFor(Object value) {
	return value + " is not a legal value";
    }

    protected E choiceFrom(String label) {
	E result = choicesByLabel.get(label);
	if (result != null) {
	    return result;
	}
	throw new IllegalArgumentException(label);
    }

    /**
     * @see net.sourceforge.pmd.PropertyDescriptor#choices()
     */
    public Object[][] choices() {

	if (choices != null) {
	    return choices;
	}

	choices = new Object[orderedLabels.length][2];

	for (int i = 0; i < choices.length; i++) {
	    choices[i][0] = orderedLabels[i];
	    choices[i][1] = choicesByLabel.get(orderedLabels[i]);
	}
	orderedLabels = null; // no longer needed
	return choices;
    }
}
