/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

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

import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Basic implementation of a property descriptor factory.
 *
 * @param <T> The type of values property descriptor returned by this factory. This can be a list.
 *
 * @author Brian Remedios
 */
public abstract class AbstractPropertyDescriptorFactory<T> implements PropertyDescriptorFactory<T> {

    protected static final Map<PropertyDescriptorField, Boolean> CORE_FIELD_TYPES_BY_KEY
        = CollectionUtil.mapFrom(new PropertyDescriptorField[] {NAME, DESCRIPTION, DEFAULT_VALUE, DELIMITER},
                                 new Boolean[] {true, true, true, false});

    private final Class<?> valueType;

    private final Map<PropertyDescriptorField, Boolean> fieldTypesByKey;


    public AbstractPropertyDescriptorFactory(Class<?> theValueType) {
        valueType = theValueType;
        fieldTypesByKey = Collections.unmodifiableMap(CORE_FIELD_TYPES_BY_KEY);
    }


    public AbstractPropertyDescriptorFactory(Class<?> theValueType, Map<PropertyDescriptorField, Boolean> additionalFieldTypesByKey) {

        valueType = theValueType;
        Map<PropertyDescriptorField, Boolean> temp
            = new HashMap<>(CORE_FIELD_TYPES_BY_KEY.size() + additionalFieldTypesByKey.size());
        temp.putAll(CORE_FIELD_TYPES_BY_KEY);
        temp.putAll(additionalFieldTypesByKey);

        fieldTypesByKey = Collections.unmodifiableMap(temp);
    }


    @Override
    public Class<?> valueType() {
        return valueType;
    }


    @Override
    public Map<PropertyDescriptorField, Boolean> expectedFields() {
        return fieldTypesByKey;
    }


    protected String nameIn(Map<PropertyDescriptorField, String> valuesById) {
        return valuesById.get(NAME);
    }


    protected String descriptionIn(Map<PropertyDescriptorField, String> valuesById) {
        return valuesById.get(DESCRIPTION);
    }


    protected String numericDefaultValueIn(Map<PropertyDescriptorField, String> valuesById) {
        String number = defaultValueIn(valuesById);
        return StringUtil.isEmpty(number) ? "0" : number; // TODO is 0 reasonable if undefined?
    }


    protected String defaultValueIn(Map<PropertyDescriptorField, String> valuesById) {
        return valuesById.get(DEFAULT_VALUE);
    }


    @Override
    public final PropertyDescriptor<T> createWith(Map<PropertyDescriptorField, String> valuesById) {
        return createWith(valuesById, false);
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
        return createWith(valuesById, true);
    }


    protected static String[] labelsIn(Map<PropertyDescriptorField, String> valuesById) {
        return StringUtil.substringsOf(valuesById.get(PropertyDescriptorField.LABELS),
                                       MultiValuePropertyDescriptor.DEFAULT_DELIMITER);
    }


    protected static Object[] choicesIn(Map<PropertyDescriptorField, String> valuesById) {
        return null; // TODO: find a way to extract an arbitrary object from a string
        // Maybe reason enough to only allow enums...
    }


    protected static int indexIn(Map<PropertyDescriptorField, String> valuesById) {
        return 0; // TODO
    }


    protected static Class<Object> classIn(Map<PropertyDescriptorField, String> valuesById) {
        return Object.class; // TODO
    }


    protected static int[] indicesIn(Map<PropertyDescriptorField, String> valuesById) {
        return null; // TODO
    }


    protected static char delimiterIn(Map<PropertyDescriptorField, String> valuesById) {
        return delimiterIn(valuesById, MultiValuePropertyDescriptor.DEFAULT_DELIMITER);
    }


    protected static char delimiterIn(Map<PropertyDescriptorField, String> valuesById, char defaultDelimiter) {
        String characterStr = "";
        if (valuesById.containsKey(DELIMITER)) {
            characterStr = valuesById.get(DELIMITER).trim();
        }
        if (StringUtil.isEmpty(characterStr)) {
            return defaultDelimiter;
        }
        return characterStr.charAt(0);
    }


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
