/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import apex.jorje.semantic.symbol.type.BasicType;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * Represents all data types that can be referenced from a Visualforce page. This enum consolidates the data types
 * available to CustomFields and Apex. It uses the naming convention of CustomFields.
 *
 * See https://developer.salesforce.com/docs/atlas.en-us.api_meta.meta/api_meta/meta_field_types.htm#meta_type_fieldtype
 */
public enum ExpressionType {
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

    private static final Logger LOGGER = Logger.getLogger(ExpressionType.class.getName());


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
    private static final Map<String, ExpressionType> CASE_INSENSITIVE_MAP = new ConcurrentHashMap<>();

    /**
     * Map of BasicType to ExpressionType. Multiple BasicTypes may map to one ExrpessionType.
     */
    private static final Map<BasicType, ExpressionType> BASIC_TYPE_MAP = new ConcurrentHashMap<>();

    static {
        for (ExpressionType expressionType : ExpressionType.values()) {
            CASE_INSENSITIVE_MAP.put(expressionType.name().toLowerCase(Locale.ROOT), expressionType);
            for (BasicType basicType : expressionType.basicTypes) {
                BASIC_TYPE_MAP.put(basicType, expressionType);
            }
        }
    }

    /**
     * Map to correct instance, returns {@code Unknown} if the value can't be mapped.
     */
    public static ExpressionType fromString(String value) {
        value = Strings.nullToEmpty(value);
        ExpressionType expressionType = CASE_INSENSITIVE_MAP.get(value.toLowerCase(Locale.ROOT));

        if (expressionType == null) {
            expressionType = ExpressionType.Unknown;
            LOGGER.fine("Unable to determine ExpressionType of " + value);
        }

        return expressionType;
    }

    /**
     * Map to correct instance, returns {@code Unknown} if the value can't be mapped.
     */
    public static ExpressionType fromBasicType(BasicType value) {
        ExpressionType expressionType = value != null ? BASIC_TYPE_MAP.get(value) : null;

        if (expressionType == null) {
            expressionType = ExpressionType.Unknown;
            LOGGER.fine("Unable to determine ExpressionType of " + value);
        }

        return expressionType;
    }

    ExpressionType(boolean requiresEscaping) {
        this(requiresEscaping, null);
    }

    ExpressionType(boolean requiresEscaping, BasicType...basicTypes) {
        this.requiresEscaping = requiresEscaping;
        this.basicTypes = Sets.newConcurrentHashSet();
        if (basicTypes != null) {
            this.basicTypes.addAll(Arrays.asList(basicTypes));
        }
    }
}
