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
 * <h1>Properties API changes</h1>This class' API is mainly provided to build GUIs for XPath rules
 * like the rule designer, so that they have info about the available properties from XML. As such,
 * the number of clients are probably low. Nevertheless, a bunch of members have been deprecated to
 * warn about probable upcoming API changes with 7.0.0, but the amount of change may be greater.
 * See {@link PropertyDescriptor} for more info about property framework changes with 7.0.0.
 *
 * @author Clément Fournier
 * @see PropertyDescriptorExternalBuilder
 * @since 6.0.0
 */
public enum PropertyTypeId {
    // These are exclusively used for XPath rules. It would make more sense to model the supported
    // property types around XML Schema Datatypes (XSD) 1.0 or 1.1 instead of Java datatypes (save for
    // e.g. the Class type), including the mnemonics (eg. xs:integer instead of Integer)

    BOOLEAN("Boolean", BooleanProperty.extractor(), ValueParserConstants.BOOLEAN_PARSER),
    /** @deprecated see {@link BooleanMultiProperty} */
    @Deprecated
    BOOLEAN_LIST("List[Boolean]", BooleanMultiProperty.extractor(), ValueParserConstants.BOOLEAN_PARSER),

    STRING("String", StringProperty.extractor(), ValueParserConstants.STRING_PARSER),
    STRING_LIST("List[String]", StringMultiProperty.extractor(), ValueParserConstants.STRING_PARSER),
    CHARACTER("Character", CharacterProperty.extractor(), ValueParserConstants.CHARACTER_PARSER),
    CHARACTER_LIST("List[Character]", CharacterMultiProperty.extractor(), ValueParserConstants.CHARACTER_PARSER),

    REGEX("Regex", RegexProperty.extractor(), ValueParserConstants.REGEX_PARSER),

    INTEGER("Integer", IntegerProperty.extractor(), ValueParserConstants.INTEGER_PARSER),
    INTEGER_LIST("List[Integer]", IntegerMultiProperty.extractor(), ValueParserConstants.INTEGER_PARSER),
    LONG("Long", LongProperty.extractor(), ValueParserConstants.LONG_PARSER),
    LONG_LIST("List[Long]", LongMultiProperty.extractor(), ValueParserConstants.LONG_PARSER),
    @Deprecated
    FLOAT("Float", FloatProperty.extractor(), ValueParserConstants.FLOAT_PARSER),
    @Deprecated
    FLOAT_LIST("List[Float]", FloatMultiProperty.extractor(), ValueParserConstants.FLOAT_PARSER),
    DOUBLE("Double", DoubleProperty.extractor(), ValueParserConstants.DOUBLE_PARSER),
    DOUBLE_LIST("List[Double]", DoubleMultiProperty.extractor(), ValueParserConstants.DOUBLE_PARSER),
    //    ENUM("Enum", EnumeratedProperty.FACTORY),                     // TODO:cf we need new syntax in the xml to support that
    //    ENUM_LIST("List[Enum]", EnumeratedMultiProperty.FACTORY),
    @Deprecated
    CLASS("Class", TypeProperty.extractor(), ValueParserConstants.CLASS_PARSER),
    @Deprecated
    CLASS_LIST("List[Class]", TypeMultiProperty.extractor(), ValueParserConstants.CLASS_PARSER);


    private static final Map<String, PropertyTypeId> CONSTANTS_BY_MNEMONIC;
    private final String stringId;
    private final PropertyDescriptorExternalBuilder<?> factory;
    private final ValueParser<?> valueParser;

    static {
        Map<String, PropertyTypeId> temp = new HashMap<>();
        for (PropertyTypeId id : values()) {
            temp.put(id.stringId, id);
        }
        CONSTANTS_BY_MNEMONIC = Collections.unmodifiableMap(temp);
    }


    PropertyTypeId(String id, PropertyDescriptorExternalBuilder<?> factory, ValueParser<?> valueParser) {
        this.stringId = id;
        this.factory = factory;
        this.valueParser = valueParser;
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
    @Deprecated
    public PropertyDescriptorExternalBuilder<?> getFactory() {
        return factory;
    }


    /**
     * Returns true if the property corresponding to this factory is numeric,
     * which means it can be safely cast to a {@link NumericPropertyDescriptor}.
     *
     * @return whether the property is numeric
     */
    @Deprecated
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
    @Deprecated
    public boolean isPropertyPackaged() {
        return factory instanceof PropertyDescriptorBuilderConversionWrapper.SingleValue.Packaged
               || factory instanceof PropertyDescriptorBuilderConversionWrapper.MultiValue.Packaged;
    }


    /**
     * Returns true if the property corresponding to this factory takes
     * lists of values as its value.
     *
     * @return whether the property is multivalue
     *
     * @deprecated see {@link PropertyDescriptor#isMultiValue()}
     */
    @Deprecated
    public boolean isPropertyMultivalue() {
        return factory.isMultiValue();
    }


    /**
     * Returns the value type of the property corresponding to this factory.
     * This is the component type of the list if the property is multivalued.
     *
     * @return The value type of the property
     *
     * @deprecated see {@link PropertyDescriptor#type()}
     */
    @Deprecated
    public Class<?> propertyValueType() {
        return factory.valueType();
    }


    /**
     * Gets the object used to parse the values of this property from a string.
     * If the property is multivalued, the parser only parses individual components
     * of the list. A list parser can be obtained with {@link ValueParserConstants#multi(ValueParser, char)}.
     *
     * @return The value parser
     *
     * @deprecated see {@link PropertyDescriptor#valueFrom(String)}
     */
    @Deprecated
    public ValueParser<?> getValueParser() {
        return valueParser;
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
     *
     * @deprecated See {@link PropertyDescriptorExternalBuilder}
     */
    @Deprecated
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
     *
     * @deprecated The signature will probably be altered in 7.0.0 but a similar functionality will be available
     */
    @Deprecated
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
