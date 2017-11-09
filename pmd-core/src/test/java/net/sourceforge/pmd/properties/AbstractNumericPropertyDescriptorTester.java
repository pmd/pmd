/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import net.sourceforge.pmd.properties.builders.MultiNumericPropertyBuilder;
import net.sourceforge.pmd.properties.builders.SingleNumericPropertyBuilder;


/**
 * @author Clément Fournier
 */
public abstract class AbstractNumericPropertyDescriptorTester<T> extends AbstractPropertyDescriptorTester<T> {

    public AbstractNumericPropertyDescriptorTester(String typeName) {
        super(typeName);
    }


    @Test
    public void testLowerUpperLimit() {
        assertNotNull(((NumericPropertyDescriptor<T>) createProperty()).lowerLimit());
        assertNotNull(((NumericPropertyDescriptor<T>) createProperty()).upperLimit());
        assertNotNull(((NumericPropertyDescriptor<T>) createMultiProperty()).lowerLimit());
        assertNotNull(((NumericPropertyDescriptor<T>) createMultiProperty()).upperLimit());
    }


    @Test(expected = RuntimeException.class)
    public void testMissingMinThreshold() {
        Map<PropertyDescriptorField, String> attributes = getPropertyDescriptorValues();
        attributes.remove(PropertyDescriptorField.MIN);
        getSingleFactory().build(attributes);
    }


    @Override
    protected Map<PropertyDescriptorField, String> getPropertyDescriptorValues() {
        Map<PropertyDescriptorField, String> attributes = super.getPropertyDescriptorValues();
        attributes.put(PropertyDescriptorField.MIN, min().toString());
        attributes.put(PropertyDescriptorField.MAX, max().toString());
        return attributes;
    }


    @Test(expected = RuntimeException.class)
    public void testMissingMaxThreshold() {
        Map<PropertyDescriptorField, String> attributes = getPropertyDescriptorValues();
        attributes.remove(PropertyDescriptorField.MAX);
        getSingleFactory().build(attributes);

    }


    @Test(expected = IllegalArgumentException.class)
    public void testBadDefaultValue() {
        singleBuilder().defaultValue(createBadValue()).build();
    }


    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testMultiBadDefaultValue() {
        multiBuilder().defaultValues(createValue(), createBadValue()).build();
    }


    protected abstract SingleNumericPropertyBuilder<T, ?> singleBuilder();

    protected abstract MultiNumericPropertyBuilder<T, ?> multiBuilder();


    protected abstract T min();

    protected abstract T max();
}
