/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.PropertyDescriptor.CORE_FIELD_TYPES_BY_KEY;
import static net.sourceforge.pmd.PropertyDescriptorField.DEFAULT_VALUE;
import static net.sourceforge.pmd.PropertyDescriptorField.DELIMITER;
import static net.sourceforge.pmd.PropertyDescriptorField.DESCRIPTION;
import static net.sourceforge.pmd.PropertyDescriptorField.LEGAL_PACKAGES;
import static net.sourceforge.pmd.PropertyDescriptorField.MAX;
import static net.sourceforge.pmd.PropertyDescriptorField.MIN;
import static net.sourceforge.pmd.PropertyDescriptorField.NAME;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.pmd.util.StringUtil;

/**
 * Basic implementation of a property descriptor factory.
 *
 * @param <T> The type of values property descriptor returned by this factory. This can be a list.
 *
 * @author Brian Remedios
 */
public abstract class AbstractPropertyDescriptorFactory<T> implements PropertyDescriptorFactory<T> {




    static {
        System.err.println(CORE_FIELD_TYPES_BY_KEY);
    }


    private final Class<?> valueType;

    /**
     * Denote the identifiers of the expected fields paired with booleans
     * denoting whether they are required (non-null) or not.
     */
    private final Map<PropertyDescriptorField, Boolean> expectedFields;


    public AbstractPropertyDescriptorFactory(Class<?> theValueType) {
        valueType = theValueType;
        expectedFields = CORE_FIELD_TYPES_BY_KEY;
    }


    public AbstractPropertyDescriptorFactory(Class<?> theValueType, Map<PropertyDescriptorField, Boolean> additionalFieldTypesByKey) {

        valueType = theValueType;
        if (additionalFieldTypesByKey == null) {
            expectedFields = CORE_FIELD_TYPES_BY_KEY;
            return;
        }
        Map<PropertyDescriptorField, Boolean> temp
            = new HashMap<>(CORE_FIELD_TYPES_BY_KEY.size() + additionalFieldTypesByKey.size());
        temp.putAll(CORE_FIELD_TYPES_BY_KEY);
        temp.putAll(additionalFieldTypesByKey);

        expectedFields = Collections.unmodifiableMap(temp);
    }


    @Override
    public Class<?> valueType() {
        return valueType;
    }


    @Override
    public Set<PropertyDescriptorField> expectableFields() {
        return Collections.unmodifiableSet(expectedFields.keySet());
    }


    /**
     * Retrieves the name of the descriptor from the map.
     *
     * @param valuesById Map of attributes
     *
     * @return The name, which is null if none is specified
     */
    protected String nameIn(Map<PropertyDescriptorField, String> valuesById) {
        return valuesById.get(NAME);
    }


    /**
     * Retrieves the description from the map.
     *
     * @param valuesById Map of attributes
     *
     * @return The description, which is null if none is specified
     */
    protected String descriptionIn(Map<PropertyDescriptorField, String> valuesById) {
        return valuesById.get(DESCRIPTION);
    }


    /**
     * Retrieves the default value from the map.
     *
     * @param valuesById Map of attributes
     *
     * @return The default value
     *
     * @throws RuntimeException if the default value is null, empty, or missing
     */
    protected String defaultValueIn(Map<PropertyDescriptorField, String> valuesById) {
        String deft = valuesById.get(DEFAULT_VALUE);
        if (StringUtil.isEmpty(deft)) {
            throw new RuntimeException("Default value was null, empty, or missing");
        }
        return deft;
    }


    @Override
    public final PropertyDescriptor<T> createWith(Map<PropertyDescriptorField, String> valuesById) {
      //  checkRequiredFields(valuesById);
        return createWith(valuesById, false);
    }


    /** Checks whether all required fields are present in the map. */
    private void checkRequiredFields(Map<PropertyDescriptorField, String> valuesById) {
        for (Entry<PropertyDescriptorField, Boolean> entry : expectedFields.entrySet()) {
            if (entry.getValue() && StringUtil.isEmpty(valuesById.get(entry.getKey()))) {
                throw new RuntimeException("Missing required value for key: " + entry.getKey());
            }
        }
    }


    /**
     * Creates a new property descriptor specifying whether the descriptor is externally defined or not. This is
     * meant to be implemented by subclasses.
     *
     * @param valuesById          The map of values
     * @param isExternallyDefined Whether the descriptor is externally defined
     *
     * @return A new and initialized {@link PropertyDescriptor}
     */
    protected abstract PropertyDescriptor<T> createWith(Map<PropertyDescriptorField, String> valuesById, boolean isExternallyDefined);


    /**
     * Checks if the value is considered as missing or not. Some properties support whitespace values, hence the
     * check. By default this does not support it. The factory can override this method to change the predicate.
     *
     * @param value The value to check
     *
     * @return True if the value must be considered missing, false otherwise
     */
    protected boolean isValueMissing(String value) {
     //   return StringUtil.isEmpty(value);
    return false;
    }


    /**
     * Creates a new property descriptor which was defined externally.
     *
     * @param valuesById The map of values
     *
     * @return A new and initialized {@link PropertyDescriptor}
     *
     * @see PropertyDescriptor#isDefinedExternally()
     */
    /* default */
    final PropertyDescriptor<T> createExternalWith(Map<PropertyDescriptorField, String> valuesById) {
        checkRequiredFields(valuesById);
        return createWith(valuesById, true);
    }


    /**
     * Gets the labels for enumerated properties, returns a string array of length 0 if none are specified.
     *
     * @param valuesById Map of attributes
     *
     * @return An array containing the labels
     */
    protected static String[] labelsIn(Map<PropertyDescriptorField, String> valuesById) {
        return StringUtil.substringsOf(valuesById.get(PropertyDescriptorField.LABELS),
                                       MultiValuePropertyDescriptor.DEFAULT_DELIMITER);
    }


    // For enumerated properties
    protected static Object[] choicesIn(Map<PropertyDescriptorField, String> valuesById) {
        throw new UnsupportedOperationException(); // TODO: find a way to extract an arbitrary object from a string
        // Maybe reason enough to only allow enums...
    }


    // For enumerated properties
    protected static int indexIn(Map<PropertyDescriptorField, String> valuesById) {
        throw new UnsupportedOperationException(); // TODO
    }


    // For enumerated properties
    protected static Class<Object> classIn(Map<PropertyDescriptorField, String> valuesById) {
        throw new UnsupportedOperationException(); // TODO
    }


    // For enumerated properties
    protected static int[] indicesIn(Map<PropertyDescriptorField, String> valuesById) {
        throw new UnsupportedOperationException(); // TODO
    }


    /**
     * Finds the delimiter in the map, taking {@link MultiValuePropertyDescriptor#DEFAULT_DELIMITER} if none is
     * mentioned.
     *
     * @param valuesById Map of attributes
     *
     * @return The delimiter or the default
     */
    protected static char delimiterIn(Map<PropertyDescriptorField, String> valuesById) {
        return delimiterIn(valuesById, MultiValuePropertyDescriptor.DEFAULT_DELIMITER);
    }


    /**
     * Finds the delimiter in the map, taking the specified default delimiter if none is specified.
     *
     * @param valuesById       Map of attributes
     * @param defaultDelimiter The default delimiter to take
     *
     * @return The delimiter or the default
     *
     * @throws RuntimeException If the delimiter is present but is more than 1 character
     */
    protected static char delimiterIn(Map<PropertyDescriptorField, String> valuesById, char defaultDelimiter) {
        String characterStr = "";
        if (valuesById.containsKey(DELIMITER)) {
            characterStr = valuesById.get(DELIMITER).trim();
        }

        if (StringUtil.isEmpty(characterStr)) {
            return defaultDelimiter;
        }

        if (characterStr.length() != 1) {
            throw new RuntimeException("Ambiguous delimiter character, must have length 1: \"" + characterStr + "\"");
        }
        return characterStr.charAt(0);
    }


    /**
     * Retrieves the minimum and maximum values from the map.
     *
     * @param valuesById Map of attributes
     *
     * @return An array of 2 string, min at the left, max at the right
     *
     * @throws RuntimeException If one of them is missing
     */
    protected static String[] minMaxFrom(Map<PropertyDescriptorField, String> valuesById) {
        String min = minValueIn(valuesById);
        String max = maxValueIn(valuesById);
        if (StringUtil.isEmpty(min) || StringUtil.isEmpty(max)) {
            throw new RuntimeException("min and max values must be specified");
        }
        return new String[] {min, max};
    }


    private static String minValueIn(Map<PropertyDescriptorField, String> valuesById) {
        return valuesById.get(MIN);
    }


    private static String maxValueIn(Map<PropertyDescriptorField, String> valuesById) {
        return valuesById.get(MAX);
    }


    protected static String[] legalPackageNamesIn(Map<PropertyDescriptorField, String> valuesById, char delimiter) {
        String names = valuesById.get(LEGAL_PACKAGES);
        if (StringUtil.isEmpty(names)) {
            return null;
        }
        return StringUtil.substringsOf(names, delimiter);
    }


    /**
     * Returns a map describing which fields are required to build an
     *
     * @param otherKeys
     * @param otherValues
     *
     * @return
     */
    public static Map<PropertyDescriptorField, Boolean> expectedFieldTypesWith(PropertyDescriptorField[] otherKeys,
                                                                               Boolean[] otherValues) {
        Map<PropertyDescriptorField, Boolean> largerMap = new HashMap<>(
            otherKeys.length + CORE_FIELD_TYPES_BY_KEY.size());
        largerMap.putAll(CORE_FIELD_TYPES_BY_KEY);
        for (int i = 0; i < otherKeys.length; i++) {
            largerMap.put(otherKeys[i], otherValues[i]);
        }
        return largerMap;
    }


}
