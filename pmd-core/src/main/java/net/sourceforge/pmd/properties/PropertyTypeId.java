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
 * the XML requires the {@code type} attribute, and the mapping between the values of
 * this attribute and the concrete property that is built is encoded in the constants
 * of this enum.
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


    private static final Map<String, PropertyDescriptorExternalBuilder<?>> DESCRIPTOR_FACTORIES_BY_TYPE;
    private final String typeId;
    private final PropertyDescriptorExternalBuilder<?> factory;

    static {
        Map<String, PropertyDescriptorExternalBuilder<?>> temp = new HashMap<>();
        for (PropertyTypeId id : values()) {
            temp.put(id.typeId, id.factory);
        }
        DESCRIPTOR_FACTORIES_BY_TYPE = Collections.unmodifiableMap(temp);
    }
    

    PropertyTypeId(String id, PropertyDescriptorExternalBuilder<?> factory) {
        this.typeId = id;
        this.factory = factory;
    }


    /**
     * Gets the value of the type attribute represented by this constant.
     *
     * @return The type id
     */
    public String getTypeId() {
        return typeId;
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
     * Returns the full mappings from type ids to factory.
     *
     * @return The full mapping.
     */
    public static Map<String, PropertyDescriptorExternalBuilder<?>> typeIdsToExtractors() {
        return DESCRIPTOR_FACTORIES_BY_TYPE;
    }


    /**
     * Gets the factory for the descriptor identified by the string id.
     *
     * @param typeId The identifier of the type
     *
     * @return The factory used to build new instances of a descriptor
     */
    public static PropertyDescriptorExternalBuilder<?> factoryFor(String typeId) {
        return DESCRIPTOR_FACTORIES_BY_TYPE.get(typeId);
    }


    /**
     * Gets the string representation of this type, as it should be given 
     * when defining a descriptor in the xml.
     *
     * @param valueType  The type to look for
     * @param multiValue Whether the descriptor is multivalued or not
     *
     * @return The type id
     */
    public static String typeIdFor(Class<?> valueType, boolean multiValue) {
        for (Map.Entry<String, PropertyDescriptorExternalBuilder<?>> entry : DESCRIPTOR_FACTORIES_BY_TYPE.entrySet()) {
            if (entry.getValue().valueType() == valueType && entry.getValue().isMultiValue() == multiValue) {
                return entry.getKey();
            }
        }
        return null;
    }
}
