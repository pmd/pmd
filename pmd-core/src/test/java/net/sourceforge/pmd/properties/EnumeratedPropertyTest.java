/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assume;
import org.junit.Test;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;

/**
 * Evaluates the functionality of the EnumeratedProperty descriptor by testing
 * its ability to catch creation errors (illegal args), flag invalid selections,
 * and serialize/deserialize selection options.
 *
 * @author Brian Remedios
 */
public class EnumeratedPropertyTest extends AbstractPropertyDescriptorTester {

    private static final String[] keys = new String[] { "map", "emptyArray", "list", "string", };

    private static final Object[] values = new Object[] { new HashMap(), new Object[0], new ArrayList(),
        "Hello World!", };

    public EnumeratedPropertyTest() {
        super("Enum");
    }

    /**
     * Method createValue.
     *
     * @param count
     *            int
     * @return Object
     */
    @Override
    protected Object createValue(int count) {

        if (count == 1) {
            return randomChoice(values);
        }

        Object[] values = new Object[count];
        for (int i = 0; i < values.length; i++) {
            values[i] = createValue(1);
        }
        return values;
    }

    /**
     * Returns a (count) number of values that are not in the set of legal
     * values.
     *
     * @param count
     *            int
     * @return Object
     */
    @Override
    protected Object createBadValue(int count) {

        if (count == 1) {
            return Integer.toString(randomInt()); // not in the set of values
        }

        Object[] values = new Object[count];
        for (int i = 0; i < values.length; i++) {
            values[i] = createBadValue(1);
        }
        return values;
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

        return multiValue
                ? new EnumeratedMultiProperty<>("testEnumerations", "Test enumerations with complex types", keys,
                        values, new int[] { 0, 1 }, 1.0f)
                : new EnumeratedProperty<>("testEnumerations", "Test enumerations with complex types", keys, values, 0,
                        1.0f);
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
                ? new EnumeratedMultiProperty<>("testEnumerations", "Test enumerations with complex types", keys,
                        new Object[0], new int[] { 99 }, 1.0f)
                : new EnumeratedProperty<>("testEnumerations", "Test enumerations with complex types", new String[0],
                        values, -1, 1.0f);
    }

    @Override
    @Test
    public void testFactorySingleValue() {
        Assume.assumeTrue("The EnumeratedProperty is not implemented completely yet", false);
    }

    @Override
    @Test
    public void testFactoryMultiValueCustomDelimiter() {
        Assume.assumeTrue("The EnumeratedProperty is not implemented completely yet", false);
    }

    @Override
    @Test
    public void testFactoryMultiValueDefaultDelimiter() {
        Assume.assumeTrue("The EnumeratedProperty is not implemented completely yet", false);
    }
}
