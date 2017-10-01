/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Enumeration;
import java.util.Map;

import net.sourceforge.pmd.properties.modules.EnumeratedPropertyModule;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Property which can take only a fixed set of values of any type, then selected via String labels. The mappings method
 * returns the set of mappings between the labels and their values.
 *
 * <p>This property currently doesn't support serialization and cannot be defined in a ruleset file.z
 *
 * @param <E> Type of the values
 *
 * @author Brian Remedios
 * @author Clément Fournier
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


    private final EnumeratedPropertyModule<E> module;


    /**
     * Constructor using arrays to define the label-value mappings. The correct construction of the property depends on
     * the correct ordering of the arrays.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param theLabels      Labels of the choices
     * @param theChoices     Values that can be chosen
     * @param defaultIndex   The index of the default value
     * @param valueType      Type of the values
     * @param theUIOrder     UI order
     *
     * @deprecated will be removed in 7.0.0. Use {@link #EnumeratedProperty(String, String, Map, Object, Class, float)}
     */
    @Deprecated
    public EnumeratedProperty(String theName, String theDescription, String[] theLabels, E[] theChoices,
                              int defaultIndex, Class<E> valueType, float theUIOrder) {
        this(theName, theDescription, CollectionUtil.mapFrom(theLabels, theChoices),
             theChoices[defaultIndex], valueType, theUIOrder, false);
    }


    /**
     * Constructor using arrays to define the label-value mappings. The correct construction of the property depends on
     * the correct ordering of the arrays.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param theLabels      Labels of the choices
     * @param theChoices     Values that can be chosen
     * @param defaultIndex   Index of the default value
     * @param theUIOrder     UI order
     *
     * @deprecated will be removed in 7.0.0. Use {@link #EnumeratedProperty(String, String, Map, Object, Class, float)}
     */
    @Deprecated
    public EnumeratedProperty(String theName, String theDescription, String[] theLabels, E[] theChoices,
                              int defaultIndex, float theUIOrder) {
        this(theName, theDescription, CollectionUtil.mapFrom(theLabels, theChoices),
             theChoices[defaultIndex], null, theUIOrder, false);
    }


    /**
     * Constructor using a map to define the label-value mappings.
     *
     * @param theName         Name
     * @param theDescription  Description
     * @param labelsToChoices Map of labels to values
     * @param defaultValue    Default value
     * @param valueType       Type of the values
     * @param theUIOrder      UI order
     */
    public EnumeratedProperty(String theName, String theDescription, Map<String, E> labelsToChoices,
                              E defaultValue, Class<E> valueType, float theUIOrder) {
        this(theName, theDescription, labelsToChoices, defaultValue, valueType, theUIOrder, false);
    }


    /** Master constructor. */
    private EnumeratedProperty(String theName, String theDescription, Map<String, E> labelsToChoices,
                               E defaultValue, Class<E> valueType, float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, defaultValue, theUIOrder, isDefinedExternally);

        module = new EnumeratedPropertyModule<>(labelsToChoices, valueType);
        module.checkValue(defaultValue);
    }


    @Override
    public Class<E> type() {
        return module.getValueType();
    }


    @Override
    public String errorFor(E value) {
        return module.errorFor(value);
    }


    @Override
    public E createFrom(String value) throws IllegalArgumentException {
        return module.choiceFrom(value);
    }


    @Override
    public String asString(E value) {
        return module.getLabelsByChoice().get(value);
    }


    @Override
    public Map<String, E> mappings() {
        return module.getChoicesByLabel(); // unmodifiable
    }


}
