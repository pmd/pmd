/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import net.sourceforge.pmd.NumericPropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptorField;

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
        getSingleFactory().createWith(attributes);
    }


    @Override
    protected Map<PropertyDescriptorField, String> getPropertyDescriptorValues() {
        Map<PropertyDescriptorField, String> attributes = super.getPropertyDescriptorValues();
        attributes.put(PropertyDescriptorField.MIN, "0");
        attributes.put(PropertyDescriptorField.MAX, "10");
        return attributes;
    }


    @Test(expected = RuntimeException.class)
    public void testMissingMaxThreshold() {
        Map<PropertyDescriptorField, String> attributes = getPropertyDescriptorValues();
        attributes.remove(PropertyDescriptorField.MAX);
        getSingleFactory().createWith(attributes);

    }
}
