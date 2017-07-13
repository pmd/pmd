/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.sourceforge.pmd.NumericPropertyDescriptor;

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


}
