/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.EnumeratedPropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.modules.EnumeratedPropertyModule;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Multi-valued property which can take only a fixed set of values of any type, then selected via String labels. The
 * mappings method returns the set of mappings between the labels and their values.
 *
 * @param <E> The type of the values
 *
 * @author Brian Remedios
 * @author Clément Fournier
 * @version Refactored June 2017 (6.0.0)
 */
public final class EnumeratedMultiProperty<E> extends AbstractMultiValueProperty<E>
    implements EnumeratedPropertyDescriptor<E, List<E>> {

    /** Factory. */
    public static final PropertyDescriptorFactory<List<Object>> FACTORY // @formatter:off
        = new MultiValuePropertyDescriptorFactory<Object>(Object.class) {  // TODO:cf is Object the right type?
            @Override
            public EnumeratedMultiProperty createWith(Map<PropertyDescriptorField, String> valuesById, boolean isDefinedExternally) {
                Object[] choices = choicesIn(valuesById);
                return new EnumeratedMultiProperty<>(nameIn(valuesById),
                                                     descriptionIn(valuesById),
                                                     CollectionUtil.mapFrom(labelsIn(valuesById), choices),
                                                     selection(indicesIn(valuesById), choices),
                                                     classIn(valuesById),
                                                     0f,
                                                     isDefinedExternally);
            }
        }; // @formatter:on

    private final EnumeratedPropertyModule<E> module;


    /**
     * Constructor using arrays to define the label-value mappings. The correct construction of the property depends on
     * the correct ordering of the arrays.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param theLabels      Labels of the choices
     * @param theChoices     Values that can be chosen
     * @param choiceIndices  Indices of the default values
     * @param valueType      Type of the values
     * @param theUIOrder     UI order
     *
     * @deprecated Use {@link #EnumeratedMultiProperty(String, String, Map, List, Class, float)}. Will be removed in
     * 7.0.0
     */
    @Deprecated
    public EnumeratedMultiProperty(String theName, String theDescription, String[] theLabels, E[] theChoices,
                                   int[] choiceIndices, Class<E> valueType, float theUIOrder) {
        this(theName, theDescription, CollectionUtil.mapFrom(theLabels, theChoices),
             selection(choiceIndices, theChoices), valueType, theUIOrder, false);
    }


    /**
     * Constructor using arrays to define the label-value mappings. The correct construction of the property depends on
     * the correct ordering of the arrays.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param theLabels      Labels of the choices
     * @param theChoices     Values that can be chosen
     * @param choiceIndices  Indices of the default values
     * @param theUIOrder     UI order
     *
     * @deprecated Use {@link #EnumeratedMultiProperty(String, String, Map, List, Class, float)}. Will be removed in
     * 7.0.0
     */
    @Deprecated
    public EnumeratedMultiProperty(String theName, String theDescription, String[] theLabels, E[] theChoices,
                                   int[] choiceIndices, float theUIOrder) {
        this(theName, theDescription, CollectionUtil.mapFrom(theLabels, theChoices),
             selection(choiceIndices, theChoices), null, theUIOrder, false);
    }


    /**
     * Constructor using a map to define the label-value mappings. The default values are specified with a list.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param choices        Map of labels to values
     * @param defaultValues  List of default values
     * @param valueType      Type of the values
     * @param theUIOrder     UI order
     */
    public EnumeratedMultiProperty(String theName, String theDescription, Map<String, E> choices,
                                   List<E> defaultValues, Class<E> valueType, float theUIOrder) {
        this(theName, theDescription, choices, defaultValues, valueType, theUIOrder, false);
    }


    private EnumeratedMultiProperty(String theName, String theDescription, Map<String, E> choices,
                                    List<E> defaultValues, Class<E> valueType, float theUIOrder,
                                    boolean isDefinedExternally) {
        super(theName, theDescription, defaultValues, theUIOrder, isDefinedExternally);

        module = new EnumeratedPropertyModule<>(choices, valueType);
        checkDefaults(defaultValues);
    }


    @Override
    public Map<String, E> mappings() {
        return module.getChoicesByLabel(); // unmodifiable
    }


    @Override
    public Class<E> type() {
        return module.getValueType();
    }


    @Override
    public String errorFor(List<E> values) {
        for (E value : values) {
            String error = module.errorFor(value);
            if (error != null) {
                return error;
            }
        }
        return null;
    }


    @Override
    protected E createFrom(String toParse) {
        return module.choiceFrom(toParse);
    }


    @Override
    public String asString(E item) {
        return module.getLabelsByChoice().get(item);
    }


    private void checkDefaults(List<E> defaults) {
        for (E elt : defaults) {
            module.checkValue(elt);
        }
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

}
