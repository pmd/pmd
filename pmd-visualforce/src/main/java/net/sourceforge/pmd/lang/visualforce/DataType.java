/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.visualforce;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents all data types that can be referenced from a Visualforce page. This enum consolidates the data types
 * available to CustomFields and Apex. It uses the naming convention of CustomFields.
 *
 * See https://developer.salesforce.com/docs/atlas.en-us.api_meta.meta/api_meta/meta_field_types.htm#meta_type_fieldtype
 */
public enum DataType {
    /**
     * @deprecated Since 7.22.0. Use {@link #AUTO_NUMBER} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    AutoNumber("AutoNumber", false),
    AUTO_NUMBER("AutoNumber", false),
    /**
     * @deprecated Since 7.22.0. Use {@link #CHECKBOX} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Checkbox("Checkbox", false, "Boolean"),
    CHECKBOX("Checkbox", false, "Boolean"),
    /**
     * @deprecated Since 7.22.0. Use {@link #CURRENCY} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Currency("Currency", false, "Currency"),
    CURRENCY("Currency", false, "Currency"),
    /**
     * @deprecated Since 7.22.0. Use {@link #DATE} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Date("Date", false, "Date"),
    DATE("Date", false, "Date"),
    /**
     * @deprecated Since 7.22.0. Use {@link #DATE_TIME} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    DateTime("DateTime", false, "Datetime"),
    DATE_TIME("DateTime", false, "Datetime"),
    /**
     * @deprecated Since 7.22.0. Use {@link #EMAIL} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Email("Email", false),
    EMAIL("Email", false),
    /**
     * @deprecated Since 7.22.0. Use {@link #ENCRYPTED_TEXT} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    EncryptedText("EncryptedText", true),
    ENCRYPTED_TEXT("EncryptedText", true),
    /**
     * @deprecated Since 7.22.0. Use {@link #EXTERNAL_LOOKUP} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    ExternalLookup("ExternalLookup", true),
    EXTERNAL_LOOKUP("ExternalLookup", true),
    /**
     * @deprecated Since 7.22.0. Use {@link #FILE} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    File("File", false),
    FILE("File", false),
    /**
     * @deprecated Since 7.22.0. Use {@link #HIERARCHY} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Hierarchy("Hierarchy", false),
    HIERARCHY("Hierarchy", false),
    /**
     * @deprecated Since 7.22.0. Use {@link #HTML} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Html("Html", false),
    HTML("Html", false),
    /**
     * @deprecated Since 7.22.0. Use {@link #INDIRECT_LOOKUP} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    IndirectLookup("IndirectLookup", false),
    INDIRECT_LOOKUP("IndirectLookup", false),
    /**
     * @deprecated Since 7.22.0. Use {@link #LOCATION} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Location("Location", false),
    LOCATION("Location", false),
    /**
     * @deprecated Since 7.22.0. Use {@link #LONG_TEXT_AREA} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    LongTextArea("LongTextArea", true),
    LONG_TEXT_AREA("LongTextArea", true),
    /**
     * @deprecated Since 7.22.0. Use {@link #LOOKUP} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Lookup("Lookup", false, "Id"),
    LOOKUP("Lookup", false, "Id"),
    /**
     * @deprecated Since 7.22.0. Use {@link #MASTER_DETAIL} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    MasterDetail("MasterDetail", false),
    MASTER_DETAIL("MasterDetail", false),
    /**
     * @deprecated Since 7.22.0. Use {@link #METADATA_RELATIONSHIP} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    MetadataRelationship("MetadataRelationship", false),
    METADATA_RELATIONSHIP("MetadataRelationship", false),
    /**
     * @deprecated Since 7.22.0. Use {@link #MULTISELECT_PICKLIST} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    MultiselectPicklist("MultiselectPicklist", true),
    MULTISELECT_PICKLIST("MultiselectPicklist", true),
    /**
     * @deprecated Since 7.22.0. Use {@link #NOTE} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Note("Note", true),
    NOTE("Note", true),
    /**
     * @deprecated Since 7.22.0. Use {@link #NUMBER} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Number("Number", false, "Decimal", "Double", "Integer", "Long"),
    NUMBER("Number", false, "Decimal", "Double", "Integer", "Long"),
    /**
     * @deprecated Since 7.22.0. Use {@link #PERCENT} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Percent("Percent", false),
    PERCENT("Percent", false),
    /**
     * @deprecated Since 7.22.0. Use {@link #PHONE} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Phone("Phone", false),
    PHONE("Phone", false),
    /**
     * @deprecated Since 7.22.0. Use {@link #PICKLIST} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Picklist("Picklist", true),
    PICKLIST("Picklist", true),
    /**
     * @deprecated Since 7.22.0. Use {@link #SUMMARY} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Summary("Summary", false),
    SUMMARY("Summary", false),
    /**
     * @deprecated Since 7.22.0. Use {@link #TEXT} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Text("Text", true, "String"),
    TEXT("Text", true, "String"),
    /**
     * @deprecated Since 7.22.0. Use {@link #TEXT_AREA} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    TextArea("TextArea", true),
    TEXT_AREA("TextArea", true),
    /**
     * @deprecated Since 7.22.0. Use {@link #TIME} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Time("Time", false, "Time"),
    TIME("Time", false, "Time"),
    /**
     * @deprecated Since 7.22.0. Use {@link #URL} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Url("Url", false),
    URL("Url", false),
    /**
     * Indicates that Metadata was found, but its type was not mappable. This could because it is a type which isn't
     * mapped, or it was an edge case where the type was ambiguously defined in the Metadata.
     *
     * @deprecated Since 7.22.0. Use {@link #UNKNOWN} instead.
     */
    @Deprecated
    @SuppressWarnings("PMD.FieldNamingConventions")
    Unknown("Unknown", true),
    /**
     * Indicates that Metadata was found, but its type was not mappable. This could because it is a type which isn't
     * mapped, or it was an edge case where the type was ambiguously defined in the Metadata.
     */
    UNKNOWN("Unknown", true);

    private static final Logger LOG = LoggerFactory.getLogger(DataType.class);


    private final String fieldTypeName;

    /**
     * @since 7.22.0
     */
    public String fieldTypeName() {
        return fieldTypeName;
    }

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
     * A map of the lower-case-normalized field type name to its instance. The case metadata is not guaranteed to have the correct
     * case.
     */
    private static final Map<String, DataType> CASE_NORMALIZED_MAP = new HashMap<>();

    /**
     * A map of the lower-case-normalized primitive type names to DataType. Multiple types may map to one DataType.
     */
    private static final Map<String, DataType> BASIC_TYPE_MAP = new HashMap<>();

    static {
        for (DataType dataType : DataType.values()) {
            try {
                Field field = DataType.class.getField(dataType.name());
                if (field.isAnnotationPresent(Deprecated.class)) {
                    continue;
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException(e);
            }

            CASE_NORMALIZED_MAP.put(dataType.fieldTypeName().toLowerCase(Locale.ROOT), dataType);
            for (String typeName : dataType.basicTypeNames) {
                BASIC_TYPE_MAP.put(typeName.toLowerCase(Locale.ROOT), dataType);
            }
        }
    }

    /**
     * Map to correct instance, returns {@code UNKNOWN} if the value can't be mapped.
     */
    public static DataType fromString(String value) {
        value = value != null ? value : "";
        DataType dataType = CASE_NORMALIZED_MAP.get(value.toLowerCase(Locale.ROOT));

        if (dataType == null) {
            dataType = DataType.UNKNOWN;
            LOG.debug("Unable to determine DataType of {}", value);
        }

        return dataType;
    }

    /**
     * Map to correct instance, returns {@code UNKNOWN} if the value can't be mapped.
     */
    public static DataType fromTypeName(String value) {
        value = value != null ? value : "";
        DataType dataType = BASIC_TYPE_MAP.get(value.toLowerCase(Locale.ROOT));

        if (dataType == null) {
            dataType = DataType.UNKNOWN;
            LOG.debug("Unable to determine DataType of {}", value);
        }

        return dataType;
    }

    DataType(String fieldTypeName, boolean requiresEscaping, String... basicTypeNames) {
        this.fieldTypeName = fieldTypeName;
        this.requiresEscaping = requiresEscaping;
        this.basicTypeNames = new HashSet<>(Arrays.asList(basicTypeNames));
    }
}
