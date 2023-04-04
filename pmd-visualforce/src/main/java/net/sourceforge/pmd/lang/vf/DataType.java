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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import apex.jorje.semantic.symbol.type.BasicType;

/**
 * Represents all data types that can be referenced from a Visualforce page. This enum consolidates the data types
 * available to CustomFields and Apex. It uses the naming convention of CustomFields.
 *
 * See https://developer.salesforce.com/docs/atlas.en-us.api_meta.meta/api_meta/meta_field_types.htm#meta_type_fieldtype
 */
public enum DataType {
    AutoNumber(false),
    Checkbox(false, "Boolean"),
    Currency(false, "Currency"),
    Date(false, "Date"),
    DateTime(false, "Datetime"),
    Email(false),
    EncryptedText(true),
    ExternalLookup(true),
    File(false),
    Hierarchy(false),
    Html(false),
    IndirectLookup(false),
    Location(false),
    LongTextArea(true),
    Lookup(false, "ID"),
    MasterDetail(false),
    MetadataRelationship(false),
    MultiselectPicklist(true),
    Note(true),
    Number(false, "Decimal", "Double", "Integer", "Long"),
    Percent(false),
    Phone(false),
    Picklist(true),
    Summary(false),
    Text(true, "String"),
    TextArea(true),
    Time(false, "Time"),
    Url(false),
    /**
     * Indicates that Metatada was found, but it's type was not mappable. This could because it is a type which isn't
     * mapped, or it was an edge case where the type was ambiguously defined in the Metadata.
     */
    Unknown(true);

    private static final Logger LOG = LoggerFactory.getLogger(DataType.class);


    /**
     * True if this field is an XSS risk
     */
    public final boolean requiresEscaping;

    /**
     * The set of primitive type names that map to this type. Multiple types can map to a single instance of this enum.
     * Note: these strings are not case-normalized.
     */
    private final Set<String> basicTypeNames;

    /**
     * A map of the lower-case-normalized enum name to its instance. The case metadata is not guaranteed to have the correct
     * case.
     */
    private static final Map<String, DataType> CASE_NORMALIZED_MAP = new HashMap<>();

    /**
     * A map of the lower-case-normalized primitive type names to DataType. Multiple types may map to one DataType.
     */
    private static final Map<String, DataType> BASIC_TYPE_MAP = new HashMap<>();

    static {
        for (DataType dataType : DataType.values()) {
            CASE_NORMALIZED_MAP.put(dataType.name().toLowerCase(Locale.ROOT), dataType);
            for (String typeName : dataType.basicTypeNames) {
                BASIC_TYPE_MAP.put(typeName.toLowerCase(Locale.ROOT), dataType);
            }
        }
    }

    /**
     * Map to correct instance, returns {@code Unknown} if the value can't be mapped.
     */
    public static DataType fromString(String value) {
        value = value != null ? value : "";
        DataType dataType = CASE_NORMALIZED_MAP.get(value.toLowerCase(Locale.ROOT));

        if (dataType == null) {
            dataType = DataType.Unknown;
            LOG.debug("Unable to determine DataType of {}", value);
        }

        return dataType;
    }

    /**
     * Map to correct instance, returns {@code Unknown} if the value can't be mapped.
     *
     * @deprecated Use {@link #fromTypeName(String)} instead.
     */
    @Deprecated
    public static DataType fromBasicType(BasicType value) {
        if (value != null) {
            switch (value) {
            case BOOLEAN:
                return Checkbox;
            case CURRENCY:
                return Currency;
            case DATE:
                return Date;
            case DATE_TIME:
                return DateTime;
            case ID:
                return Lookup;
            case DECIMAL:
            case DOUBLE:
            case INTEGER:
            case LONG:
                return Number;
            case STRING:
                return Text;
            case TIME:
                return Time;
            default:
                break;
            }
        }
        LOG.debug("Unable to determine DataType of {}", value);
        return Unknown;
    }

    /**
     * Map to correct instance, returns {@code Unknown} if the value can't be mapped.
     */
    public static DataType fromTypeName(String value) {
        value = value != null ? value : "";
        DataType dataType = BASIC_TYPE_MAP.get(value.toLowerCase(Locale.ROOT));

        if (dataType == null) {
            dataType = DataType.Unknown;
            LOG.debug("Unable to determine DataType of {}", value);
        }

        return dataType;
    }

    DataType(boolean requiresEscaping) {
        this(requiresEscaping, null);
    }

    DataType(boolean requiresEscaping, String... basicTypeNames) {
        this.requiresEscaping = requiresEscaping;
        this.basicTypeNames = new HashSet<>();
        if (basicTypeNames != null) {
            this.basicTypeNames.addAll(Arrays.asList(basicTypeNames));
        }
    }
}
