/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.EnumeratedPropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Multi-valued property which can take only a fixed set of values of any type, then selected via String labels. The
 * choices method returns the set of mappings between the labels and their values.
 *
 * @param <E> The type of the values
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class EnumeratedMultiProperty<E> extends AbstractMultiValueProperty<E>
    implements EnumeratedPropertyDescriptor<E, List<E>> {

    /** Factory. */
    public static final PropertyDescriptorFactory FACTORY // @formatter:off
        = new BasicPropertyDescriptorFactory<Enumeration>(Enumeration.class) {
            @Override
            public EnumeratedMultiProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
                return new EnumeratedMultiProperty<>(nameIn(valuesById),
                                                     descriptionIn(valuesById),
                                                     labelsIn(valuesById),
                                                     choicesIn(valuesById),
                                                     indicesIn(valuesById),
                                                     classIn(valuesById),
                                                     0f);
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
     * @param choiceIndices  The indices of the default values.
     * @param theUIOrder     UI order
     */
    public EnumeratedMultiProperty(String theName, String theDescription, String[] theLabels, E[] theChoices,
                                   int[] choiceIndices, Class<E> valueType, float theUIOrder) {
        this(theName, theDescription, CollectionUtil.mapFrom(theLabels, theChoices),
             selection(choiceIndices, theChoices), valueType, theUIOrder);
    }


    /**
     * Constructor using a map to define the label-value mappings. The default values are specified with a list.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param choices        Map of labels to values
     * @param defaultValues  List of default values
     * @param theUIOrder     UI order
     */
    public EnumeratedMultiProperty(String theName, String theDescription, Map<String, E> choices,
                                   List<E> defaultValues, Class<E> valueType, float theUIOrder) {
        super(theName, theDescription, defaultValues, theUIOrder);

        checkDefaults(defaultValues, choices);

        this.valueType = valueType;
        choicesByLabel = Collections.unmodifiableMap(choices);
        labelsByChoice = Collections.unmodifiableMap(CollectionUtil.invertedMapFrom(choicesByLabel));
    }


    private static <E> List<E> selection(int[] choiceIndices, E[] theChoices) {
        List<E> selected = new ArrayList<>();
        for (int i : choiceIndices) {
            if (i < 0 || i > theChoices.length) {
                throw new IllegalArgumentException("Default value index is out of bounds: " + i);
            }
            selected.add(theChoices[i]);
        }
        return selected;
    }


    private static <E> void checkDefaults(List<E> defaults, Map<String, E> choices) {
        for (E elt : defaults) {
            if (!choices.containsValue(elt)) {
                throw new IllegalArgumentException("Invalid default value: no mapping to this value");
            }
        }
    }


    @Override
    public Map<String, E> mappings() {
        return choicesByLabel;
    }


    @Override
    public Class<E> type() {
        return valueType;
    }


    @Override
    public String errorFor(List<E> values) {
        for (E value : values) {
            if (!labelsByChoice.containsKey(value)) {
                return nonLegalValueMsgFor(value);
            }
        }
        return null;
    }


    private String nonLegalValueMsgFor(E value) {
        return value + " is not a legal value";
    }


    @Override
    protected E createFrom(String toParse) {
        return choiceFrom(toParse);
    }


    private E choiceFrom(String label) {
        E result = choicesByLabel.get(label);
        if (result == null) {
            throw new IllegalArgumentException(label);
        }
        return result;
    }


    @Override
    public String asString(E item) {
        return labelsByChoice.get(item);
    }

}
