/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.NumericPropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;

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


    @Test
    @SuppressWarnings("unchecked")
    public void testWrapper() {
        NumericPropertyDescriptor<T> prop = (NumericPropertyDescriptor<T>) createProperty();
        NumericPropertyDescriptor<List<T>> multi = (NumericPropertyDescriptor<List<T>>) createMultiProperty();

        NumericPropertyDescriptor<T> propW = null;
        NumericPropertyDescriptor<List<T>> multiW = null;
        try {
            propW = (NumericPropertyDescriptor<T>) PropertyDescriptorWrapper.getWrapper(prop);
            multiW = (NumericPropertyDescriptor<List<T>>) PropertyDescriptorWrapper.getWrapper(multi);
        } catch (ClassCastException ioe) {
            fail();
        }

        assertEquals(prop.lowerLimit(), propW.lowerLimit());
        assertEquals(prop.upperLimit(), propW.upperLimit());
        assertEquals(multi.lowerLimit(), multiW.lowerLimit());
        assertEquals(multi.upperLimit(), multiW.upperLimit());
    }

}
