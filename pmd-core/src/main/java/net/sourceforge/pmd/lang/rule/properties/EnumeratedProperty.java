/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import net.sourceforge.pmd.EnumeratedPropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
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
 * @version Refactored June 2017 (6.0.0)
 */
public final class EnumeratedProperty<E> extends AbstractSingleValueProperty<E>
    implements EnumeratedPropertyDescriptor<E, E> {

    /** Factory. */
    public static final PropertyDescriptorFactory<? extends Enumeration> FACTORY // @formatter:off
        = new SingleValuePropertyDescriptorFactory<Enumeration>(Enumeration.class) { // TODO:cf Enumeration? Object?

            @Override
            public EnumeratedProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
                return new EnumeratedProperty<>(nameIn(valuesById),
                                                descriptionIn(valuesById),
                                                labelsIn(valuesById),   // this is not implemented
                                                choicesIn(valuesById),  // ditto
                                                indexIn(valuesById),    // ditto
                                                classIn(valuesById),
                                                0f);
            }
        }; // @formatter:on


    private final Map<String, E> choicesByLabel;
    private final Map<E, String> labelsByChoice;
    private final Class<E> valueType;


    public EnumeratedProperty(String theName, String theDescription, String[] theLabels, E[] theChoices,
                              int defaultIndex, Class<E> valueType, float theUIOrder) {
        this(theName, theDescription, CollectionUtil.mapFrom(theLabels, theChoices),
             theChoices[defaultIndex], valueType, theUIOrder);
    }


    public EnumeratedProperty(String theName, String theDescription, Map<String, E> labelsToChoices,
                              E defaultValue, Class<E> valueType, float theUIOrder) {
        super(theName, theDescription, defaultValue, theUIOrder);

        this.valueType = valueType;
        choicesByLabel = Collections.unmodifiableMap(labelsToChoices);
        labelsByChoice = Collections.unmodifiableMap(CollectionUtil.invertedMapFrom(choicesByLabel));
    }


    @Override
    public Class<E> type() {
        return valueType;
    }


    @Override
    public String errorFor(Object value) {
        return labelsByChoice.containsKey(value) ? null : nonLegalValueMsgFor(value);
    }


    private String nonLegalValueMsgFor(Object value) {
        return value + " is not a legal value";
    }


    @Override
    public E createFrom(String value) throws IllegalArgumentException {
        return choiceFrom(value);
    }


    private E choiceFrom(String label) {
        E result = choicesByLabel.get(label);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException(label);
    }


    @Override
    public String asString(E value) {
        return labelsByChoice.get(value);
    }


    @Override
    public Map<String, E> mappings() {
        return choicesByLabel;
    }

}
