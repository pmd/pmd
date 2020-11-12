/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import apex.jorje.semantic.symbol.type.BasicType;

/**
 * Represents all data types that can be referenced from a Visualforce page. This enum consolidates the data types
 * available to CustomFields and Apex. It uses the naming convention of CustomFields.
 *
 * See https://developer.salesforce.com/docs/atlas.en-us.api_meta.meta/api_meta/meta_field_types.htm#meta_type_fieldtype
 */
public enum IdentifierType {
    AutoNumber(false),
    Checkbox(false, BasicType.BOOLEAN),
    Currency(false, BasicType.CURRENCY),
    Date(false, BasicType.DATE),
    DateTime(false, BasicType.DATE_TIME),
    Email(false),
    EncryptedText(true),
    ExternalLookup(true),
    File(false),
    Hierarchy(false),
    Html(true),
    IndirectLookup(false),
    Location(false),
    LongTextArea(true),
    Lookup(false, BasicType.ID),
    MasterDetail(false),
    MetadataRelationship(false),
    MultiselectPicklist(true),
    Note(true),
    Number(false, BasicType.DECIMAL, BasicType.DOUBLE, BasicType.INTEGER, BasicType.LONG),
    Percent(false),
    Phone(false),
    Picklist(true),
    Summary(false),
    Text(true, BasicType.STRING),
    TextArea(true),
    Time(false, BasicType.TIME),
    Url(false),
    Unknown(true);

    private static final Logger LOGGER = Logger.getLogger(IdentifierType.class.getName());


    /**
     * True if this field is an XSS risk
     */
    public final boolean requiresEscaping;

    /**
     * The set of {@link BasicType}s that map to this type. Multiple types can map to a single instance of this enum.
     */
    private final Set<BasicType> basicTypes;

    /**
     * A case insensitive map of the enum name to its instance. The case metadata is not guaranteed to have the correct
     * case.
     */
    private static final Map<String, IdentifierType> CASE_INSENSITIVE_MAP = new HashMap<>();

    /**
     * Map of BasicType to IdentifierType. Multiple BasicTypes may map to one ExrpessionType.
     */
    private static final Map<BasicType, IdentifierType> BASIC_TYPE_MAP = new HashMap<>();

    static {
        for (IdentifierType identifierType : IdentifierType.values()) {
            CASE_INSENSITIVE_MAP.put(identifierType.name().toLowerCase(Locale.ROOT), identifierType);
            for (BasicType basicType : identifierType.basicTypes) {
                BASIC_TYPE_MAP.put(basicType, identifierType);
            }
        }
    }

    /**
     * Map to correct instance, returns {@code Unknown} if the value can't be mapped.
     */
    public static IdentifierType fromString(String value) {
        value = value != null ? value : "";
        IdentifierType identifierType = CASE_INSENSITIVE_MAP.get(value.toLowerCase(Locale.ROOT));

        if (identifierType == null) {
            identifierType = IdentifierType.Unknown;
            LOGGER.fine("Unable to determine IdentifierType of " + value);
        }

        return identifierType;
    }

    /**
     * Map to correct instance, returns {@code Unknown} if the value can't be mapped.
     */
    public static IdentifierType fromBasicType(BasicType value) {
        IdentifierType identifierType = value != null ? BASIC_TYPE_MAP.get(value) : null;

        if (identifierType == null) {
            identifierType = IdentifierType.Unknown;
            LOGGER.fine("Unable to determine IdentifierType of " + value);
        }

        return identifierType;
    }

    IdentifierType(boolean requiresEscaping) {
        this(requiresEscaping, null);
    }

    IdentifierType(boolean requiresEscaping, BasicType...basicTypes) {
        this.requiresEscaping = requiresEscaping;
        this.basicTypes = new HashSet<>();
        if (basicTypes != null) {
            this.basicTypes.addAll(Arrays.asList(basicTypes));
        }
    }
}
