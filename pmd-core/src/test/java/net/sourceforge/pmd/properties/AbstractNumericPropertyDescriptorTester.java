/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.properties.builders.MultiNumericPropertyBuilder;
import net.sourceforge.pmd.properties.builders.SingleNumericPropertyBuilder;


/**
 * @author Clément Fournier
 */
abstract class AbstractNumericPropertyDescriptorTester<T> extends AbstractPropertyDescriptorTester<T> {

    AbstractNumericPropertyDescriptorTester(String typeName) {
        super(typeName);
    }


    @Test
    void testLowerUpperLimit() {
        assertNotNull(((NumericPropertyDescriptor<T>) createProperty()).lowerLimit());
        assertNotNull(((NumericPropertyDescriptor<T>) createProperty()).upperLimit());
        assertNotNull(((NumericPropertyDescriptor<T>) createMultiProperty()).lowerLimit());
        assertNotNull(((NumericPropertyDescriptor<T>) createMultiProperty()).upperLimit());
    }


    @Test
    void testMissingMinThreshold() {
        Map<PropertyDescriptorField, String> attributes = getPropertyDescriptorValues();
        attributes.remove(PropertyDescriptorField.MIN);
        assertThrows(RuntimeException.class, () -> getSingleFactory().build(attributes));
    }


    @Override
    protected Map<PropertyDescriptorField, String> getPropertyDescriptorValues() {
        Map<PropertyDescriptorField, String> attributes = super.getPropertyDescriptorValues();
        attributes.put(PropertyDescriptorField.MIN, min().toString());
        attributes.put(PropertyDescriptorField.MAX, max().toString());
        return attributes;
    }


    @Test
    void testMissingMaxThreshold() {
        Map<PropertyDescriptorField, String> attributes = getPropertyDescriptorValues();
        attributes.remove(PropertyDescriptorField.MAX);
        assertThrows(RuntimeException.class, () -> getSingleFactory().build(attributes));
    }


    @Test
    void testBadDefaultValue() {
        assertThrows(IllegalArgumentException.class, () -> singleBuilder().defaultValue(createBadValue()).build());
    }


    @Test
    @SuppressWarnings("unchecked")
    void testMultiBadDefaultValue() {
        assertThrows(IllegalArgumentException.class, () -> multiBuilder().defaultValues(createValue(), createBadValue()).build());
    }


    protected abstract SingleNumericPropertyBuilder<T, ?> singleBuilder();

    protected abstract MultiNumericPropertyBuilder<T, ?> multiBuilder();


    protected abstract T min();

    protected abstract T max();
}
