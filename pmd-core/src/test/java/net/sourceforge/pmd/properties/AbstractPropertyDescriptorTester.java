/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorUtil;

/**
 * Base functionality for all concrete subclasses that evaluate type-specific
 * property descriptors. Checks for error conditions during construction, error
 * value detection, serialization, etc.
 *
 * @author Brian Remedios
 */
public abstract class AbstractPropertyDescriptorTester<T> {

    public static final String PUNCTUATION_CHARS = "!@#$%^&*()_-+=[]{}\\|;:'\",.<>/?`~";
    public static final String WHITESPACE_CHARS = " \t\n";
    public static final String DIGIST_CHARS = "0123456789";
    public static final String ALPHA_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmniopqrstuvwxyz";
    public static final String ALPHA_NUMERIC_CHARS = DIGIST_CHARS + ALPHA_CHARS;
    public static final String ALL_CHARS = PUNCTUATION_CHARS + WHITESPACE_CHARS + ALPHA_NUMERIC_CHARS;
    private static final int MULTI_VALUE_COUNT = 10;
    protected final String typeName;


    public AbstractPropertyDescriptorTester(String typeName) {
        this.typeName = typeName;
    }


    protected abstract PropertyDescriptor<List<T>> createBadMultiProperty();


    @Test
    public void testFactorySingleValue() {
        PropertyDescriptor<T> prop = getSingleFactory().createWith(getPropertyDescriptorValues());
        T originalValue = createValue();
        T value = prop.valueFrom(
            originalValue instanceof Class ? ((Class) originalValue).getName() : String.valueOf(originalValue));
        String asDelimitedString = prop.asDelimitedString(value);
        Object value2 = prop.valueFrom(asDelimitedString);
        assertEquals(value, value2);
    }


    @SuppressWarnings("unchecked")
    protected final PropertyDescriptorFactory<T> getSingleFactory() {
        return (PropertyDescriptorFactory<T>) PropertyDescriptorUtil.factoryFor(typeName);
    }


    private Map<PropertyDescriptorField, String> getPropertyDescriptorValues() {
        Map<PropertyDescriptorField, String> valuesById = new HashMap<>();
        valuesById.put(PropertyDescriptorField.NAME, "test");
        valuesById.put(PropertyDescriptorField.DESCRIPTION, "desc");
        valuesById.put(PropertyDescriptorField.MIN, "0");
        valuesById.put(PropertyDescriptorField.MAX, "10");
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
        PropertyDescriptorFactory<List<T>> multiFactory = getMultiFactory();
        PropertyDescriptor<List<T>> prop = multiFactory.createWith(getPropertyDescriptorValues());
        List<T> originalValue = createMultipleValues(MULTI_VALUE_COUNT);
        String asDelimitedString = prop.asDelimitedString(originalValue);
        List<T> value2 = prop.valueFrom(asDelimitedString);
        assertEquals(originalValue, value2);
    }


    @SuppressWarnings("unchecked")
    protected final PropertyDescriptorFactory<List<T>> getMultiFactory() {
        return (PropertyDescriptorFactory<List<T>>) PropertyDescriptorUtil.factoryFor("List<" + typeName + ">");
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
        PropertyDescriptorFactory<List<T>> multiFactory = getMultiFactory();
        Map<PropertyDescriptorField, String> valuesById = getPropertyDescriptorValues();
        String customDelimiter = "ä";
        assertFalse(ALL_CHARS.contains(customDelimiter));
        valuesById.put(PropertyDescriptorField.DELIMITER, customDelimiter);
        PropertyDescriptor<List<T>> prop = multiFactory.createWith(valuesById);
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
     * Attempt to create a property with faulty configuration values. This
     * method should throw an IllegalArgumentException if done correctly.
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

        assertEquals(returnedValue, testValue);
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
     * Return a value(s) that is known to be faulty per the general scope of the
     * descriptor.
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
    public void testType() {
        assertNotNull(createMultiProperty().type());
        assertNotNull(createProperty().type());
    }


    public static boolean randomBool() {
        return ((Math.random() * 100) % 2) == 0;
    }


    public static int randomInt() {

        int randomVal = (int) (Math.random() * 100 + 1D);
        return randomVal + (int) (Math.random() * 100000D);
    }


    public static String randomString(int length) {

        final char[] chars = ALPHA_CHARS.toCharArray();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(randomChar(chars));
        }
        return sb.toString();
    }


    public static char randomChar(char[] characters) {
        return characters[randomInt(0, characters.length - 1)];
    }


    public static int randomInt(int min, int max) {
        if (max < min) {
            max = min;
        }
        int range = Math.abs(max - min);
        int x = (int) (range * Math.random());
        return x + min;
    }


    public static float randomFloat(float min, float max) {

        return (float) randomDouble(min, max);
    }


    public static double randomDouble(double min, double max) {
        if (max < min) {
            max = min;
        }
        double range = Math.abs(max - min);
        double x = range * Math.random();
        return x + min;
    }


    public static long randomLong(long min, long max) {
        if (max < min) {
            max = min;
        }
        long range = Math.abs(max - min);
        long x = (long) (range * Math.random());
        return x + min;
    }


    /**
     * Method randomChoice.
     *
     * @param items Object[]
     *
     * @return Object
     */
    public static <T> T randomChoice(T[] items) {
        return items[randomInt(0, items.length - 1)];
    }


    /**
     * Method filter.
     *
     * @param chars      char[]
     * @param removeChar char
     *
     * @return char[]
     */
    protected static final char[] filter(char[] chars, char removeChar) {
        int count = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == removeChar) {
                count++;
            }
        }
        char[] results = new char[chars.length - count];

        int index = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != removeChar) {
                results[index++] = chars[i];
            }
        }
        return results;
    }
}
