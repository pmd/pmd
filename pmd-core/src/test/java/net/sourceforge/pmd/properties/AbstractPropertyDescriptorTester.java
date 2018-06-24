/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import org.junit.Test;

import net.sourceforge.pmd.properties.builders.PropertyDescriptorExternalBuilder;


/**
 * Base functionality for all concrete subclasses that evaluate type-specific property descriptors. Checks for error
 * conditions during construction, error value detection, serialization, etc.
 *
 * @author Brian Remedios
 */
public abstract class AbstractPropertyDescriptorTester<T> {

    public static final String PUNCTUATION_CHARS = "!@#$%^&*()_-+=[]{}\\|;:'\",.<>/?`~";
    public static final String WHITESPACE_CHARS = " \t\n";
    public static final String DIGIT_CHARS = "0123456789";
    public static final String ALPHA_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmniopqrstuvwxyz";
    public static final String ALPHA_NUMERIC_CHARS = DIGIT_CHARS + ALPHA_CHARS;
    public static final String ALL_CHARS = PUNCTUATION_CHARS + WHITESPACE_CHARS + ALPHA_NUMERIC_CHARS;
    private static final int MULTI_VALUE_COUNT = 10;

    private static final Random RANDOM = new Random();

    protected final String typeName;


    public AbstractPropertyDescriptorTester(String typeName) {
        this.typeName = typeName;
    }


    protected abstract PropertyDescriptor<List<T>> createBadMultiProperty();


    @Test
    public void testFactorySingleValue() {
        PropertyDescriptor<T> prop = getSingleFactory().build(getPropertyDescriptorValues());
        T originalValue = createValue();
        T value = prop.valueFrom(originalValue instanceof Class ? ((Class) originalValue).getName() : String.valueOf(originalValue));
        T value2 = prop.valueFrom(prop.asDelimitedString(value));
        if (Pattern.class.equals(prop.type())) {
            // Pattern.equals uses object identity...
            // we're forced to do that to make it compare the string values of the pattern
            assertEquals(String.valueOf(value), String.valueOf(value2));
        } else {
            assertEquals(value, value2);
        }
    }


    @SuppressWarnings("unchecked")
    protected final PropertyDescriptorExternalBuilder<T> getSingleFactory() {
        return (PropertyDescriptorExternalBuilder<T>) PropertyTypeId.factoryFor(typeName);
    }


    protected Map<PropertyDescriptorField, String> getPropertyDescriptorValues() {
        Map<PropertyDescriptorField, String> valuesById = new HashMap<>();
        valuesById.put(PropertyDescriptorField.NAME, "test");
        valuesById.put(PropertyDescriptorField.DESCRIPTION, "desc");
        valuesById.put(PropertyDescriptorField.DEFAULT_VALUE, createProperty().asDelimitedString(createValue()));
        return valuesById;
    }


    /**
     * Return a legal value(s) per the general scope of the descriptor.
     *
     * @return Object
     */
    protected abstract T createValue();


    @Test
    public void testFactoryMultiValueDefaultDelimiter() {
        PropertyDescriptorExternalBuilder<List<T>> multiFactory = getMultiFactory();
        PropertyDescriptor<List<T>> prop = multiFactory.build(getPropertyDescriptorValues());
        List<T> originalValue = createMultipleValues(MULTI_VALUE_COUNT);
        String asDelimitedString = prop.asDelimitedString(originalValue);
        List<T> value2 = prop.valueFrom(asDelimitedString);
        assertEquals(originalValue, value2);
    }


    @SuppressWarnings("unchecked")
    protected final PropertyDescriptorExternalBuilder<List<T>> getMultiFactory() {
        return (PropertyDescriptorExternalBuilder<List<T>>) PropertyTypeId.factoryFor("List[" + typeName + "]");
    }


    private List<T> createMultipleValues(int count) {
        List<T> res = new ArrayList<>();
        while (count > 0) {
            res.add(createValue());
            count--;
        }
        return res;
    }


    @Test
    public void testFactoryMultiValueCustomDelimiter() {
        PropertyDescriptorExternalBuilder<List<T>> multiFactory = getMultiFactory();
        Map<PropertyDescriptorField, String> valuesById = getPropertyDescriptorValues();
        String customDelimiter = "ä";
        assertFalse(ALL_CHARS.contains(customDelimiter));
        valuesById.put(PropertyDescriptorField.DELIMITER, customDelimiter);
        PropertyDescriptor<List<T>> prop = multiFactory.build(valuesById);
        List<T> originalValue = createMultipleValues(MULTI_VALUE_COUNT);
        String asDelimitedString = prop.asDelimitedString(originalValue);
        List<T> value2 = prop.valueFrom(asDelimitedString);
        assertEquals(originalValue.toString(), value2.toString());
        assertEquals(originalValue, value2);
    }


    @Test
    public void testConstructors() {

        PropertyDescriptor<T> desc = createProperty();
        assertNotNull(desc);

        try {
            createBadProperty();
        } catch (Exception ex) {
            return; // caught ok
        }

        fail("uncaught constructor exception");
    }


    /**
     * Creates and returns a properly configured property descriptor.
     *
     * @return PropertyDescriptor
     */
    protected abstract PropertyDescriptor<T> createProperty();


    /**
     * Attempt to create a property with faulty configuration values. This method should throw an
     * IllegalArgumentException if done correctly.
     *
     * @return PropertyDescriptor
     */
    protected abstract PropertyDescriptor<T> createBadProperty();


    @Test
    public void testAsDelimitedString() {

        List<T> testValue = createMultipleValues(MULTI_VALUE_COUNT);
        PropertyDescriptor<List<T>> pmdProp = createMultiProperty();

        String storeValue = pmdProp.asDelimitedString(testValue);
        List<T> returnedValue = pmdProp.valueFrom(storeValue);

        assertEquals(returnedValue, testValue);
    }


    protected abstract PropertyDescriptor<List<T>> createMultiProperty();


    @Test
    public void testValueFrom() {

        T testValue = createValue();
        PropertyDescriptor<T> pmdProp = createProperty();

        String storeValue = pmdProp.asDelimitedString(testValue);

        T returnedValue = pmdProp.valueFrom(storeValue);

        if (Pattern.class.equals(pmdProp.type())) {
            // Pattern.equals uses object identity...
            // we're forced to do that to make it compare the string values of the pattern
            assertEquals(String.valueOf(returnedValue), String.valueOf(testValue));
        } else {
            assertEquals(returnedValue, testValue);
        }
    }


    @Test
    public void testErrorForCorrectSingle() {
        T testValue = createValue();
        PropertyDescriptor<T> pmdProp = createProperty(); // plain vanilla
        // property & valid test value
        String errorMsg = pmdProp.errorFor(testValue);
        assertNull(errorMsg, errorMsg);
    }


    @Test
    public void testErrorForCorrectMulti() {
        List<T> testMultiValues = createMultipleValues(MULTI_VALUE_COUNT); // multi-value property, all
        // valid test values
        PropertyDescriptor<List<T>> multiProperty = createMultiProperty();
        String errorMsg = multiProperty.errorFor(testMultiValues);
        assertNull(errorMsg, errorMsg);

    }


    @Test
    public void testErrorForBadSingle() {
        T testValue = createBadValue();
        PropertyDescriptor<T> pmdProp = createProperty(); // plain vanilla
        // property & valid test value
        String errorMsg = pmdProp.errorFor(testValue);
        assertNotNull("uncaught bad value: " + testValue, errorMsg);
    }


    /**
     * Return a value(s) that is known to be faulty per the general scope of the descriptor.
     *
     * @return Object
     */
    protected abstract T createBadValue();


    @Test
    public void testErrorForBadMulti() {
        List<T> testMultiValues = createMultipleBadValues(MULTI_VALUE_COUNT); // multi-value property, all
        // valid test values
        PropertyDescriptor<List<T>> multiProperty = createMultiProperty();
        String errorMsg = multiProperty.errorFor(testMultiValues);
        assertNotNull("uncaught bad value in: " + testMultiValues, errorMsg);
    }


    private List<T> createMultipleBadValues(int count) {
        List<T> res = new ArrayList<>();
        while (count > 0) {
            res.add(createBadValue());
            count--;
        }
        return res;
    }


    @Test
    public void testIsMultiValue() {
        assertFalse(createProperty().isMultiValue());
    }


    @Test
    public void testIsMultiValueMulti() {
        assertTrue(createMultiProperty().isMultiValue());
    }

    @Test
    public void testAddAttributes() {
        Map<PropertyDescriptorField, String> atts = createProperty().attributeValuesById();
        assertTrue(atts.containsKey(PropertyDescriptorField.NAME));
        assertTrue(atts.containsKey(PropertyDescriptorField.DESCRIPTION));
        assertTrue(atts.containsKey(PropertyDescriptorField.DEFAULT_VALUE));
    }


    @Test
    public void testAddAttributesMulti() {
        Map<PropertyDescriptorField, String> multiAtts = createMultiProperty().attributeValuesById();
        assertTrue(multiAtts.containsKey(PropertyDescriptorField.DELIMITER));
        assertTrue(multiAtts.containsKey(PropertyDescriptorField.NAME));
        assertTrue(multiAtts.containsKey(PropertyDescriptorField.DESCRIPTION));
        assertTrue(multiAtts.containsKey(PropertyDescriptorField.DEFAULT_VALUE));
    }


    @Test
    public void testType() {
        assertNotNull(createProperty().type());
    }


    @Test
    public void testTypeMulti() {
        assertNotNull(createMultiProperty().type());
    }

    static boolean randomBool() {
        return RANDOM.nextBoolean();
    }


    static char randomChar(char[] characters) {
        return characters[randomInt(0, characters.length)];
    }


    static int randomInt(int min, int max) {
        return (int) randomLong(min, max);
    }


    static float randomFloat(float min, float max) {
        return (float) randomDouble(min, max);
    }


    static double randomDouble(double min, double max) {
        return min + RANDOM.nextDouble() * Math.abs(max - min);
    }


    static long randomLong(long min, long max) {
        return min + RANDOM.nextInt((int) Math.abs(max - min));
    }


    static <T> T randomChoice(T[] items) {
        return items[randomInt(0, items.length)];
    }


    /**
     * Method filter.
     *
     * @param chars      char[]
     * @param removeChar char
     * @return char[]
     */
    protected static char[] filter(char[] chars, char removeChar) {
        int count = 0;
        for (char c : chars) {
            if (c == removeChar) {
                count++;
            }
        }
        char[] results = new char[chars.length - count];

        int index = 0;
        for (char c : chars) {
            if (c != removeChar) {
                results[index++] = c;
            }
        }
        return results;
    }
}
