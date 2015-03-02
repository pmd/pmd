/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.util.CollectionUtil;

/**
 * @author Brian Remedios
 * @param <T>
 */
public abstract class AbstractEnumeratedProperty<E, T> extends AbstractProperty<T> {

    protected Map<String, E> choicesByLabel;
    protected Map<E, String> labelsByChoice;

    private String[] orderedLabels;
    protected Object[][] choices;

    /**
     * @param theName
     * @param theDescription
     * @param theLabels
     * @param theChoices
     * @param choiceIndices
     * @param theUIOrder
     * @param isMulti
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    public AbstractEnumeratedProperty(String theName, String theDescription, String[] theLabels, E[] theChoices,
            int[] choiceIndices, float theUIOrder, boolean isMulti) {
        super(theName, theDescription, (T) selectionsIn(theLabels, choiceIndices, isMulti), theUIOrder);

        choicesByLabel = CollectionUtil.mapFrom(theLabels, theChoices);
        labelsByChoice = CollectionUtil.invertedMapFrom(choicesByLabel);
        orderedLabels = theLabels;
    }

    /**
     * Method selectionsIn.
     * 
     * @param items String[]
     * @param selectionIndices int[]
     * @param isMulti boolean
     * @return Object
     */
    private static Object selectionsIn(String[] items, int[] selectionIndices, boolean isMulti) {
        String[] selections = new String[selectionIndices.length];
        final int maxIdx = items.length - 1;
        for (int i = 0; i < selections.length; i++) {
            if (i < 0 || i > maxIdx) {
                throw new IllegalArgumentException("Invalid item index: " + i);
            }
            selections[i] = items[selectionIndices[i]];
        }
        return isMulti ? selections : selections[0];
    }

    /**
     * @return String
     */
    protected String defaultAsString() {

        return isMultiValue() ? (String) defaultValue() : asDelimitedString(defaultValue(), '|');
    }

    /**
     * Method nonLegalValueMsgFor.
     * 
     * @param value Object
     * @return String
     */
    protected String nonLegalValueMsgFor(Object value) {
        return value + " is not a legal value";
    }

    /**
     * Method choiceFrom.
     * 
     * @param label String
     * @return E
     */
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
