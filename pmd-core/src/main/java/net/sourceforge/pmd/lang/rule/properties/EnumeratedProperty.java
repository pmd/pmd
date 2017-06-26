/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
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

    /** Factory. */
    public static final PropertyDescriptorFactory<? extends Enumeration> FACTORY
        = new BasicPropertyDescriptorFactory<Enumeration>(Enumeration.class) {

        @Override
        public EnumeratedProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
            return new EnumeratedProperty<>(nameIn(valuesById),
                                            descriptionIn(valuesById),
                                            labelsIn(valuesById), // those are not implemented
                                            choicesIn(valuesById), // ditto
                                            indexIn(valuesById), // ditto
                                            0f);
        }
    };
    private Map<String, E> choicesByLabel;
    private Map<E, String> labelsByChoice;


    public EnumeratedProperty(String theName, String theDescription, String[] theLabels, E[] theChoices,
                              int defaultIndex, float theUIOrder) {
        this(theName, theDescription, CollectionUtil.mapFrom(theLabels, theChoices), theChoices[defaultIndex],
             theUIOrder);
    }


    public EnumeratedProperty(String theName, String theDescription, Map<String, E> labelsToChoices,
                              E defaultValue, float theUIOrder) {
        super(theName, theDescription, defaultValue, theUIOrder);

        choicesByLabel = Collections.unmodifiableMap(labelsToChoices);
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
