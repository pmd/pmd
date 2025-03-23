/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.sourceforge.pmd.properties.InternalApiBridge;
import net.sourceforge.pmd.properties.PropertyBuilder;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.properties.PropertySerializer;


/**
 * Enumerates the properties that can be built from the XML. Defining a property in
 * the XML requires the {@code type} attribute, which acts as a mnemonic for the type
 * of the property that should be built. The mapping between the values of this attribute
 * and the concrete property that is built is encoded in the constants of this enum.
 *
 * <p>This class' API is mainly provided to build GUIs for XPath rules
 * like the rule designer, so that they have info about the available properties from XML. As such,
 * the number of clients are probably low. Fow now, this stays as Internal API and might be
 * changed.
 *
 * @author Clément Fournier
 * @since 6.0.0
 * @apiNote Internal API
 */
public enum PropertyTypeId {
    // These are exclusively used for XPath rules. It would make more sense to model the supported
    // property types around XML Schema Datatypes (XSD) 1.0 or 1.1 instead of Java datatypes (save for
    // e.g. the Class type), including the mnemonics (eg. xs:integer instead of Integer)

    BOOLEAN("Boolean", PropertyParsingUtil.BOOLEAN, PropertyFactory::booleanProperty),
    STRING("String", PropertyParsingUtil.STRING, PropertyFactory::stringProperty),
    STRING_LIST("List[String]", PropertyParsingUtil.STRING_LIST, PropertyFactory::stringListProperty),
    CHARACTER("Character", PropertyParsingUtil.CHARACTER, PropertyFactory::charProperty),
    CHARACTER_LIST("List[Character]", PropertyParsingUtil.CHAR_LIST, PropertyFactory::charListProperty),

    REGEX("Regex", PropertyParsingUtil.REGEX, PropertyFactory::regexProperty),

    INTEGER("Integer", PropertyParsingUtil.INTEGER, PropertyFactory::intProperty),
    INTEGER_LIST("List[Integer]", PropertyParsingUtil.INTEGER_LIST, PropertyFactory::intListProperty),
    LONG("Long", PropertyParsingUtil.LONG, PropertyFactory::longIntProperty),
    LONG_LIST("List[Long]", PropertyParsingUtil.LONG_LIST, PropertyFactory::longIntListProperty),
    DOUBLE("Double", PropertyParsingUtil.DOUBLE, PropertyFactory::doubleProperty),
    DOUBLE_LIST("List[Double]", PropertyParsingUtil.DOUBLE_LIST, PropertyFactory::doubleListProperty),
    ;  // SUPPRESS CHECKSTYLE enum trailing semi is awesome


    private static final Map<String, PropertyTypeId> CONSTANTS_BY_MNEMONIC;
    private final String stringId;
    private final PropertySerializer<?> propertySerializer;
    private final Function<String, ? extends PropertyBuilder<?, ?>> factory;


    static {
        Map<String, PropertyTypeId> temp = new HashMap<>();
        for (PropertyTypeId id : values()) {
            temp.put(id.stringId, id);
        }
        CONSTANTS_BY_MNEMONIC = Collections.unmodifiableMap(temp);
    }


    <T> PropertyTypeId(String id, PropertySerializer<T> syntax, Function<String, PropertyBuilder<?, T>> factory) {
        this.stringId = id;
        this.propertySerializer = syntax;
        this.factory = factory;
    }

    /**
     * An factory for new properties, whose default value must be deserialized
     * using an {@link PropertySerializer}. This is provided so that the mapper and
     * the factory may be related through the same type parameter, so that
     * capture works well.
     *
     * @param <T> Type of values of the property.
     */
    public interface BuilderAndMapper<T> {

        PropertySerializer<T> getXmlMapper();

        PropertyBuilder<?, T> newBuilder(String name);
    }

    /**
     * Returns the object used to create new properties with the type
     * of this constant.
     */
    @SuppressWarnings("rawtypes")
    public BuilderAndMapper<?> getBuilderUtils() {
        return new BuilderAndMapper() {
            @Override
            public PropertySerializer<?> getXmlMapper() {
                return propertySerializer;
            }

            @Override
            public PropertyBuilder<?, ?> newBuilder(String name) {
                PropertyBuilder<?, ?> builder = factory.apply(name);
                builder = InternalApiBridge.withTypeId(builder, PropertyTypeId.this);
                return builder.availableInXPath(true);
            }
        };
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
     * Returns the full mappings from type ids to enum constants.
     *
     * @return The full mapping.
     */
    public static Map<String, PropertyTypeId> typeIdsToConstants() {
        return CONSTANTS_BY_MNEMONIC;
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


}
