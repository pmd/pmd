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
 * Property which can take only a fixed set of values of any type, then selected via String labels. The
 * mappings method returns the set of mappings between the labels and their values.
 *
 * <p>This property currently doesn't support serialization and cannot be defined in a ruleset file.z
 *
 * @param <E> Type of the values
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
            public EnumeratedProperty createWith(Map<PropertyDescriptorField, String> valuesById, boolean isDefinedExternally) {
                Map<String, Object> labelsToChoices = CollectionUtil.mapFrom(labelsIn(valuesById),   // this is not implemented
                                                                            choicesIn(valuesById));  // ditto
                return new EnumeratedProperty<>(nameIn(valuesById),
                                                descriptionIn(valuesById),
                                                labelsToChoices,
                                                choicesIn(valuesById)[indexIn(valuesById)],
                                                classIn(valuesById),
                                                0f,
                                                isDefinedExternally);
            }
        }; // @formatter:on


    private final Map<String, E> choicesByLabel;
    private final Map<E, String> labelsByChoice;
    private final Class<E> valueType;


    /**
     * Constructor using arrays to define the label-value mappings. The correct construction of the property depends
     * on the correct ordering of the arrays.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param theLabels      Labels of the choices
     * @param theChoices     Values that can be chosen
     * @param defaultIndex   The index of the default value.
     * @param theUIOrder     UI order
     *
     * @deprecated will be removed in 7.0.0. Use a map.
     */
    public EnumeratedProperty(String theName, String theDescription, String[] theLabels, E[] theChoices,
                              int defaultIndex, Class<E> valueType, float theUIOrder) {
        this(theName, theDescription, CollectionUtil.mapFrom(theLabels, theChoices),
             theChoices[defaultIndex], valueType, theUIOrder, false);
    }


    private EnumeratedProperty(String theName, String theDescription, Map<String, E> labelsToChoices,
                               E defaultValue, Class<E> valueType, float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, defaultValue, theUIOrder, isDefinedExternally);

        this.valueType = valueType;
        choicesByLabel = Collections.unmodifiableMap(labelsToChoices);
        labelsByChoice = Collections.unmodifiableMap(CollectionUtil.invertedMapFrom(choicesByLabel));
    }


    /**
     * Constructor using a map to define the label-value mappings.
     *
     * @param theName         Name
     * @param theDescription  Description
     * @param labelsToChoices Map of labels to values
     * @param defaultValue    Default value
     * @param theUIOrder      UI order
     */
    public EnumeratedProperty(String theName, String theDescription, Map<String, E> labelsToChoices,
                              E defaultValue, Class<E> valueType, float theUIOrder) {
        this(theName, theDescription, labelsToChoices, defaultValue, valueType, theUIOrder, false);
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
        return choicesByLabel; // unmodifiable
    }


}
