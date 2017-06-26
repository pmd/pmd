/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties.factories;

import static net.sourceforge.pmd.PropertyDescriptorField.DEFAULT_VALUE;
import static net.sourceforge.pmd.PropertyDescriptorField.DELIMITER;
import static net.sourceforge.pmd.PropertyDescriptorField.DESC;
import static net.sourceforge.pmd.PropertyDescriptorField.LEGAL_PACKAGES;
import static net.sourceforge.pmd.PropertyDescriptorField.MAX;
import static net.sourceforge.pmd.PropertyDescriptorField.MIN;
import static net.sourceforge.pmd.PropertyDescriptorField.NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.AbstractProperty;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.StringUtil;

/**
 * @param <T>
 *
 * @author Brian Remedios
 */
public class BasicPropertyDescriptorFactory<T> implements PropertyDescriptorFactory<T> {

    protected static final Map<PropertyDescriptorField, Boolean> CORE_FIELD_TYPES_BY_KEY
        = CollectionUtil.mapFrom(new PropertyDescriptorField[] {NAME, DESC, DEFAULT_VALUE, DELIMITER}, new Boolean[] {true, true, true, false});

    private final Class<?> valueType;

    private final Map<PropertyDescriptorField, Boolean> fieldTypesByKey;


    public BasicPropertyDescriptorFactory(Class<?> theValueType) {
        valueType = theValueType;
        fieldTypesByKey = Collections.unmodifiableMap(CORE_FIELD_TYPES_BY_KEY);
    }


    public BasicPropertyDescriptorFactory(Class<?> theValueType, Map<PropertyDescriptorField, Boolean> additionalFieldTypesByKey) {

        valueType = theValueType;
        Map<PropertyDescriptorField, Boolean> temp = new HashMap<>(
            CORE_FIELD_TYPES_BY_KEY.size() + additionalFieldTypesByKey.size());
        temp.putAll(CORE_FIELD_TYPES_BY_KEY);
        temp.putAll(additionalFieldTypesByKey);

        fieldTypesByKey = Collections.unmodifiableMap(temp);
    }


    private static String minValueIn(Map<PropertyDescriptorField, String> valuesById) {
        return valuesById.get(MIN);
    }


    private static String maxValueIn(Map<PropertyDescriptorField, String> valuesById) {
        return valuesById.get(MAX);
    }


    /**
     * Parses a string into a list of values of type {@literal <U>}.
     *
     * @param toParse   The string to parse
     * @param delimiter The delimiter to use
     * @param extractor The function mapping a string to an instance of {@code <U>}
     * @param <U>       The type of the values to parse
     *
     * @return A list of values
     */
    protected static <U> List<U> parsePrimitives(String toParse, char delimiter, ValueParser<U> extractor) {
        String[] values = StringUtil.substringsOf(toParse, delimiter);
        List<U> result = new ArrayList<>();
        for (String s : values) {
            result.add(extractor.valueOf(s));
        }
        return result;
    }


    protected static Boolean[] booleanValuesIn(String booleanString, char delimiter) {
        String[] values = StringUtil.substringsOf(booleanString, delimiter);

        Boolean[] result = new Boolean[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Boolean.valueOf(values[i]);
        }
        return result;
    }


    protected static Long[] longsIn(String numberString, char delimiter) {
        String[] values = StringUtil.substringsOf(numberString, delimiter);
        List<Long> longs = new ArrayList<>(values.length);
        for (String value : values) {
            try {
                Long newLong = Long.parseLong(value);
                longs.add(newLong);
            } catch (Exception ex) {

            }
        }
        return longs.toArray(new Long[longs.size()]);
    }


    protected static Float[] floatsIn(String numberString, char delimiter) {
        String[] values = StringUtil.substringsOf(numberString, delimiter);
        List<Float> floats = new ArrayList<>(values.length);
        for (String value : values) {
            try {
                Float newFloat = Float.parseFloat(value);
                floats.add(newFloat);
            } catch (Exception ex) {

            }
        }
        return floats.toArray(new Float[floats.size()]);
    }


    protected static Double[] doublesIn(String numberString, char delimiter) {
        String[] values = StringUtil.substringsOf(numberString, delimiter);
        List<Double> doubles = new ArrayList<>(values.length);
        for (String value : values) {
            try {
                Double newDouble = Double.parseDouble(value);
                doubles.add(newDouble);
            } catch (Exception ex) {

            }
        }
        return doubles.toArray(new Double[doubles.size()]);
    }


    protected static String[] labelsIn(Map<PropertyDescriptorField, String> valuesById) {
        return null; // TODO
    }


    protected static Object[] choicesIn(Map<PropertyDescriptorField, String> valuesById) {
        return null; // TODO
    }


    protected static int indexIn(Map<PropertyDescriptorField, String> valuesById) {
        return 0; // TODO
    }

    // protected static T[] primitivesFrom(String text, WrapperBuilder<T>
    // builder) {
    //
    // String[] values = text.split(","); // TODO
    // List items = new ArrayList(values.length);
    // for (String value : values) {
    // try {
    // Object newIten = builder.itemFrom(value);
    // items.add(newIten);
    // } catch (Exception ex) {
    //
    // }
    // }
    // return items.toArray(builder.newArray(items.size()));
    // }


    protected static int[] indicesIn(Map<PropertyDescriptorField, String> valuesById) {
        return null; // TODO
    }


    protected static char delimiterIn(Map<PropertyDescriptorField, String> valuesById) {
        return delimiterIn(valuesById, AbstractProperty.DEFAULT_DELIMITER);
    }


    protected static char delimiterIn(Map<PropertyDescriptorField, String> valuesById, char defaultDelimiter) {
        String characterStr = "";
        if (valuesById.containsKey(DELIMITER)) {
            characterStr = valuesById.get(DELIMITER).trim();
        }
        if (characterStr.isEmpty()) {
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


    protected static String[] legalPackageNamesIn(Map<PropertyDescriptorField, String> valuesById, char delimiter) {
        String names = valuesById.get(LEGAL_PACKAGES);
        if (StringUtil.isEmpty(names)) {
            return null;
        }
        return StringUtil.substringsOf(names, delimiter);
    }


    public static Map<PropertyDescriptorField, Boolean> expectedFieldTypesWith(PropertyDescriptorField[] otherKeys, Boolean[]
        otherValues) {
        Map<PropertyDescriptorField, Boolean> largerMap = new HashMap<>(
            otherKeys.length + CORE_FIELD_TYPES_BY_KEY.size());
        largerMap.putAll(CORE_FIELD_TYPES_BY_KEY);
        for (int i = 0; i < otherKeys.length; i++) {
            largerMap.put(otherKeys[i], otherValues[i]);
        }
        return largerMap;
    }


    @Override
    public Class<?> valueType() {
        return valueType;
    }


    @Override
    public PropertyDescriptor<T> createWith(Map<PropertyDescriptorField, String> valuesById) {
        throw new RuntimeException("Unimplemented createWith() method in subclass");
    }


    @Override
    public Map<PropertyDescriptorField, Boolean> expectedFields() {
        return fieldTypesByKey;
    }


    protected String nameIn(Map<PropertyDescriptorField, String> valuesById) {
        return valuesById.get(NAME);
    }


    protected String descriptionIn(Map<PropertyDescriptorField, String> valuesById) {
        return valuesById.get(DESC);
    }


    protected String defaultValueIn(Map<PropertyDescriptorField, String> valuesById) {
        return valuesById.get(DEFAULT_VALUE);
    }


    protected String numericDefaultValueIn(Map<PropertyDescriptorField, String> valuesById) {
        String number = defaultValueIn(valuesById);
        return StringUtil.isEmpty(number) ? "0" : number; // TODO is 0
        // reasonable if
        // undefined?
    }

    // protected static Map<String, PropertyDescriptorFactory>
    // factoriesByTypeIdFrom(PropertyDescriptorFactory[] factories) {
    // Map<String, PropertyDescriptorFactory> factoryMap = new HashMap<String,
    // PropertyDescriptorFactory>(factories.length);
    // for (PropertyDescriptorFactory factory : factories)
    // factoryMap.put(factory.typeId(), factory);
    // return factoryMap;
    // }
    //
}
