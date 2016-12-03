/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import org.junit.Test;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.BooleanMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;

/**
 * @author Brian Remedios
 */
public class BooleanPropertyTest extends AbstractPropertyDescriptorTester {

    public BooleanPropertyTest() {
        super("Boolean");
    }

    /**
     * Method createValue.
     *
     * @param valueCount
     *            int
     * @return Object
     */
    @Override
    protected Object createValue(int valueCount) {

        if (valueCount == 1) {
            return System.currentTimeMillis() % 1 > 0 ? Boolean.TRUE : Boolean.FALSE;
        }

        Boolean[] values = new Boolean[valueCount];
        for (int i = 0; i < values.length; i++) {
            values[i] = (Boolean) createValue(1);
        }
        return values;
    }

    @Override
    @Test
    public void testErrorForBad() {
        // override, cannot create a 'bad' boolean per se
    }

    @Override
    protected Object createBadValue(int count) {
        return null;
    }

    /**
     * Method createProperty.
     *
     * @param multiValue
     *            boolean
     * @return PropertyDescriptor
     */
    @Override
    protected PropertyDescriptor createProperty(boolean multiValue) {
        return multiValue ? new BooleanMultiProperty("testBoolean", "Test boolean property",
                new Boolean[] { false, true, true }, 1.0f)
                : new BooleanProperty("testBoolean", "Test boolean property", false, 1.0f);
    }

    /**
     * Method createBadProperty.
     *
     * @param multiValue
     *            boolean
     * @return PropertyDescriptor
     */
    @Override
    protected PropertyDescriptor createBadProperty(boolean multiValue) {
        return multiValue
                ? new BooleanMultiProperty("", "Test boolean property", new Boolean[] { false, true, true }, 1.0f)
                : new BooleanProperty("testBoolean", "", false, 1.0f);
    }
}
