/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.sourceforge.pmd.properties.xml.XmlMapper;
import net.sourceforge.pmd.properties.xml.XmlSyntaxUtils;


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
 * @since 6.0.0
 */
public enum PropertyTypeId {
    // These are exclusively used for XPath rules. It would make more sense to model the supported
    // property types around XML Schema Datatypes (XSD) 1.0 or 1.1 instead of Java datatypes (save for
    // e.g. the Class type), including the mnemonics (eg. xs:integer instead of Integer)

    BOOLEAN("Boolean", XmlSyntaxUtils.BOOLEAN, PropertyFactory::booleanProperty),
    STRING("String", XmlSyntaxUtils.STRING, PropertyFactory::stringProperty),
    STRING_LIST("List[String]", XmlSyntaxUtils.STRING_LIST, PropertyFactory::stringListProperty),
    CHARACTER("Character", XmlSyntaxUtils.CHARACTER, PropertyFactory::charProperty),
    CHARACTER_LIST("List[Character]", XmlSyntaxUtils.CHAR_LIST, PropertyFactory::charListProperty),

    REGEX("Regex", XmlSyntaxUtils.REGEX, PropertyFactory::regexProperty),

    INTEGER("Integer", XmlSyntaxUtils.INTEGER, PropertyFactory::intProperty),
    INTEGER_LIST("List[Integer]", XmlSyntaxUtils.INTEGER_LIST, PropertyFactory::intListProperty),
    LONG("Long", XmlSyntaxUtils.LONG, PropertyFactory::longIntProperty),
    LONG_LIST("List[Long]", XmlSyntaxUtils.LONG_LIST, PropertyFactory::longIntListProperty),
    DOUBLE("Double", XmlSyntaxUtils.DOUBLE, PropertyFactory::doubleProperty),
    DOUBLE_LIST("List[Double]", XmlSyntaxUtils.DOUBLE_LIST, PropertyFactory::doubleListProperty),
    ;  // SUPPRESS CHECKSTYLE enum trailing semi is awesome


    private static final Map<String, PropertyTypeId> CONSTANTS_BY_MNEMONIC;
    private final String stringId;
    private final XmlMapper<?> xmlMapper;
    private final Function<String, ? extends PropertyBuilder<?, ?>> factory;


    static {
        Map<String, PropertyTypeId> temp = new HashMap<>();
        for (PropertyTypeId id : values()) {
            temp.put(id.stringId, id);
        }
        CONSTANTS_BY_MNEMONIC = Collections.unmodifiableMap(temp);
    }


    <T> PropertyTypeId(String id, XmlMapper<T> syntax, Function<String, PropertyBuilder<?, T>> factory) {
        this.stringId = id;
        this.xmlMapper = syntax;
        this.factory = factory;
    }

    /**
     * An factory for new properties, whose default value must be deserialized
     * using an {@link XmlMapper}. This is provided so that the mapper and
     * the factory may be related through the same type parameter, so that
     * capture works well.
     *
     * @param <T> Type of values of the property.
     */
    public interface BuilderAndMapper<T> {

        XmlMapper<T> getXmlMapper();

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
            public XmlMapper<?> getXmlMapper() {
                return xmlMapper;
            }

            @Override
            public PropertyBuilder<?, ?> newBuilder(String name) {
                return factory.apply(name).typeId(PropertyTypeId.this).availableInXPath(true);
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
