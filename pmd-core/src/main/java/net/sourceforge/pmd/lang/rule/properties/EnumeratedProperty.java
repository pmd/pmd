/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Defines a datatype with a set of preset values of any type as held within a
 * pair of maps. While the values are not serialized out, the labels are and
 * serve as keys to obtain the values. The choices() method provides the ordered
 * selections to be used in an editor widget.
 *
 * @param <E> Type of the choices
 *
 * @author Brian Remedios
 */
public class EnumeratedProperty<E> extends AbstractSingleValueProperty<E> {

    public static final PropertyDescriptorFactory FACTORY
        = new BasicPropertyDescriptorFactory<Enumeration>(Enumeration.class) {

        @Override
        public EnumeratedProperty createWith(Map<String, String> valuesById) {
            return new EnumeratedProperty<>(nameIn(valuesById), descriptionIn(valuesById), labelsIn(valuesById),
                                            choicesIn(valuesById), indexIn(valuesById), 0f);
        }
    };
    protected Map<String, E> choicesByLabel;
    protected Map<E, String> labelsByChoice;

    /**
     * Constructor for EnumeratedProperty.
     *
     * @param theName        String
     * @param theDescription String
     * @param theLabels      String[]
     * @param theChoices     E[]
     * @param defaultIndex   int
     * @param theUIOrder     float
     *
     * @throws IllegalArgumentException
     */
    public EnumeratedProperty(String theName, String theDescription, String[] theLabels, E[] theChoices,
                              int defaultIndex, float theUIOrder) {
        super(theName, theDescription, theChoices[defaultIndex], theUIOrder);

        choicesByLabel = CollectionUtil.mapFrom(theLabels, theChoices);
        labelsByChoice = CollectionUtil.invertedMapFrom(choicesByLabel);
    }


    @Override
    public Class<Object> type() {
        return Object.class;
    }


    @Override
    public String errorFor(Object value) {
        return labelsByChoice.containsKey(value) ? null : nonLegalValueMsgFor(value);
    }


    private String nonLegalValueMsgFor(Object value) {
        return value + " is not a legal value";
    }


    private E choiceFrom(String label) {
        E result = choicesByLabel.get(label);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException(label);
    }


    @Override
    public E createFrom(String value) throws IllegalArgumentException {
        return choiceFrom(value);
    }

    @Override
    public String asString(E value) {
        return labelsByChoice.get(value);
    }

    @Override
    public Set<Entry<String, E>> choices() {
        return choicesByLabel.entrySet();
    }

}
