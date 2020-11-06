/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Map;

import net.sourceforge.pmd.properties.builders.SingleValuePropertyBuilder;
import net.sourceforge.pmd.properties.modules.EnumeratedPropertyModule;
import net.sourceforge.pmd.util.CollectionUtil;


/**
 * Property which can take only a fixed set of values of any type, then selected via String labels. The mappings method
 * returns the set of mappings between the labels and their values.
 *
 * <p>This property currently doesn't support serialization and cannot be defined in a ruleset file.z </p>
 *
 * @param <E> Type of the values
 *
 * @author Brian Remedios
 * @author Clément Fournier
 * @version Refactored June 2017 (6.0.0)
 * @deprecated Use a {@code PropertyDescriptor<E>} instead. A builder is available from {@link PropertyFactory#enumProperty(String, Map)}.
 *             This class will be removed in 7.0.0.
 */
@Deprecated
public final class EnumeratedProperty<E> extends AbstractSingleValueProperty<E>
        implements EnumeratedPropertyDescriptor<E, E> {

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
     * @deprecated Use {@link PropertyFactory#enumProperty(String, Map)}
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
     * @deprecated Use {@link PropertyFactory#enumProperty(String, Map)}
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
     * @deprecated Use {@link PropertyFactory#enumProperty(String, Map)}
     */
    @Deprecated
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


    /**
     * @deprecated Use {@link PropertyFactory#enumProperty(String, Map)}
     */
    @Deprecated
    public static <E> EnumPBuilder<E> named(String name) {
        return new EnumPBuilder<>(name);
    }


    /**
     * @deprecated Use {@link PropertyFactory#enumProperty(String, Map)}
     */
    @Deprecated
    public static final class EnumPBuilder<E> extends SingleValuePropertyBuilder<E, EnumPBuilder<E>> {

        private Class<E> valueType;
        private Map<String, E> mappings;


        private EnumPBuilder(String name) {
            super(name);
        }

        public EnumPBuilder<E> type(Class<E> type) {
            this.valueType = type;
            return this;
        }

        public EnumPBuilder<E> mappings(Map<String, E> map) {
            this.mappings = map;
            return this;
        }


        @Override
        public EnumeratedProperty<E> build() {
            return new EnumeratedProperty<>(this.name, this.description, mappings, this.defaultValue, valueType, this.uiOrder, isDefinedInXML);
        }
    }
}
