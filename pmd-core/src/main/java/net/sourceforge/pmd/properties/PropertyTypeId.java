/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorExternalBuilder;


/**
 * Enumerates the properties that can be built from the XML. Defining a property in
 * the XML requires the {@code type} attribute, which acts as a mnemonic for the type
 * of the property that should be built. The mapping between the values of this attribute
 * and the concrete property that is built is encoded in the constants of this enum.
 *
 * @author Clément Fournier
 * @see PropertyDescriptorExternalBuilder
 * @since 6.0.0
 */
public enum PropertyTypeId {
    BOOLEAN("Boolean", BooleanProperty.extractor()),
    BOOLEAN_LIST("List[Boolean]", BooleanMultiProperty.extractor()),

    STRING("String", StringProperty.extractor()),
    STRING_LIST("List[String]", StringMultiProperty.extractor()),
    CHARACTER("Character", CharacterProperty.extractor()),
    CHARACTER_LIST("List[Character]", CharacterMultiProperty.extractor()),

    INTEGER("Integer", IntegerProperty.extractor()),
    INTEGER_LIST("List[Integer]", IntegerMultiProperty.extractor()),
    LONG("Long", LongProperty.extractor()),
    LONG_LIST("List[Long]", LongMultiProperty.extractor()),
    FLOAT("Float", FloatProperty.extractor()),
    FLOAT_LIST("List[Float]", FloatMultiProperty.extractor()),
    DOUBLE("Double", DoubleProperty.extractor()),
    DOUBLE_LIST("List[Double]", DoubleMultiProperty.extractor()),
    //    ENUM("Enum", EnumeratedProperty.FACTORY),                     // TODO:cf we need new syntax in the xml to support that
    //    ENUM_LIST("List[Enum]", EnumeratedMultiProperty.FACTORY),

    CLASS("Class", TypeProperty.extractor()),
    CLASS_LIST("List[Class]", TypeMultiProperty.extractor());


    private static final Map<String, PropertyTypeId> CONSTANTS_BY_MNEMONIC;
    private final String stringId;
    private final PropertyDescriptorExternalBuilder<?> factory;

    static {
        Map<String, PropertyTypeId> temp = new HashMap<>();
        for (PropertyTypeId id : values()) {
            temp.put(id.stringId, id);
        }
        CONSTANTS_BY_MNEMONIC = Collections.unmodifiableMap(temp);
    }


    PropertyTypeId(String id, PropertyDescriptorExternalBuilder<?> factory) {
        this.stringId = id;
        this.factory = factory;
    }


    /**
     * Gets the value of the type attribute represented by this constant.
     *
     * @return The string id
     */
    public String getStringId() {
        return stringId;
    }


    /**
     * Gets the factory associated to the type id, that can build the
     * property from strings extracted from the XML.
     *
     * @return The factory
     */
    public PropertyDescriptorExternalBuilder<?> getFactory() {
        return factory;
    }


    /**
     * Returns true if the property corresponding to this factory is numeric,
     * which means it can be safely cast to a {@link NumericPropertyDescriptor}.
     *
     * @return whether the property is numeric
     */
    public boolean isPropertyNumeric() {
        return factory instanceof PropertyDescriptorBuilderConversionWrapper.SingleValue.Numeric
               || factory instanceof PropertyDescriptorBuilderConversionWrapper.MultiValue.Numeric;
    }


    /**
     * Returns true if the property corresponding to this factory is packaged,
     * which means it can be safely cast to a {@link PackagedPropertyDescriptor}.
     *
     * @return whether the property is packaged
     */
    public boolean isPropertyPackaged() {
        return factory instanceof PropertyDescriptorBuilderConversionWrapper.SingleValue.Packaged
               || factory instanceof PropertyDescriptorBuilderConversionWrapper.MultiValue.Packaged;
    }


    /**
     * Returns true if the property corresponding to this factory takes
     * lists of values as its value.
     *
     * @return whether the property is multivalue
     */
    public boolean isPropertyMultivalue() {
        return factory.isMultiValue();
    }


    /**
     * Returns the value type of the property corresponding to this factory.
     * This is the component type of the list if the property is multivalued.
     *
     * @return The value type of the property
     */
    public Class<?> propertyValueType() {
        return factory.valueType();
    }


    /**
     * Returns the full mappings from type ids to enum constants.
     *
     * @return The full mapping.
     */
    public static Map<String, PropertyTypeId> typeIdsToConstants() {
        return CONSTANTS_BY_MNEMONIC;
    }


    /**
     * Gets the factory for the descriptor identified by the string id.
     *
     * @param stringId The identifier of the type
     *
     * @return The factory used to build new instances of a descriptor
     */
    public static PropertyDescriptorExternalBuilder<?> factoryFor(String stringId) {
        PropertyTypeId cons = CONSTANTS_BY_MNEMONIC.get(stringId);
        return cons == null ? null : cons.factory;
    }


    /**
     * Gets the enum constant corresponding to the given mnemonic.
     *
     * @param stringId A mnemonic for the property type
     *
     * @return A PropertyTypeId
     */
    public static PropertyTypeId lookupMnemonic(String stringId) {
        return CONSTANTS_BY_MNEMONIC.get(stringId);
    }


    /**
     * Gets the string representation of this type, as it should be given
     * when defining a descriptor in the xml.
     *
     * @param valueType  The type to look for
     * @param multiValue Whether the descriptor is multivalued or not
     *
     * @return The string id
     */
    public static String typeIdFor(Class<?> valueType, boolean multiValue) {
        for (Map.Entry<String, PropertyTypeId> entry : CONSTANTS_BY_MNEMONIC.entrySet()) {
            if (entry.getValue().propertyValueType() == valueType
                && entry.getValue().isPropertyMultivalue() == multiValue) {
                return entry.getKey();
            }
        }
        return null;
    }
}
