/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Defines a datatype with a set of preset values of any type as held within a
 * pair of maps. While the values are not serialized out, the labels are and
 * serve as keys to obtain the values. The choices() method provides the ordered
 * selections to be used in an editor widget.
 *
 * @param <E>
 *
 * @author Brian Remedios
 */
public class EnumeratedMultiProperty<E> extends AbstractMultiValueProperty<E> {

    public static final PropertyDescriptorFactory FACTORY
        = new BasicPropertyDescriptorFactory<Enumeration>(Enumeration.class) {

        @Override
        public EnumeratedMultiProperty createWith(Map<String, String> valuesById) {

            return new EnumeratedMultiProperty<>(nameIn(valuesById), descriptionIn(valuesById), labelsIn(valuesById),
                                                 choicesIn(valuesById), indicesIn(valuesById), 0f);
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
     * @param choiceIndices  int[]
     * @param theUIOrder     float
     *
     * @throws IllegalArgumentException
     */
    public EnumeratedMultiProperty(String theName, String theDescription, String[] theLabels, E[] theChoices,
                                   int[] choiceIndices, float theUIOrder) {
        super(theName, theDescription, selection(choiceIndices, theChoices), theUIOrder);
        choicesByLabel = CollectionUtil.mapFrom(theLabels, theChoices);
        labelsByChoice = CollectionUtil.invertedMapFrom(choicesByLabel);
    }

    private static <E> List<E> selection(int[] choiceIndices, E[] theChoices) {
        List<E> selected = new ArrayList<>();
        for (int i : choiceIndices) {
            selected.add(theChoices[i]);
        }
        return selected;
    }


    @Override
    public Class<Enumeration> type() {
        return Enumeration.class;
    }


    private String nonLegalValueMsgFor(E value) {
        return value + " is not a legal value";
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

    private E choiceFrom(String label) {
        E result = choicesByLabel.get(label);
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException(label);
    }

    @Override
    protected E createFrom(String toParse) {
        return choiceFrom(toParse);
    }

    @Override
    public String asString(E item) {
        return labelsByChoice.get(item);
    }
}
