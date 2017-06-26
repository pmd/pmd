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

    private static final String[] KEYS = new String[] {"map", "emptyArray", "list", "string",};

    private static final Object[] VALUES = new Object[] {new HashMap(), new Object[0], new ArrayList(),
                                                         "Hello World!",};

    public EnumeratedPropertyTest() {
        super("Enum");
    }


    @Override
    protected Object createValue() {
        return randomChoice(VALUES);
    }


    @Override
    protected Object createBadValue() {
        return Integer.toString(randomInt()); // not in the set of values
    }

    @Override
    protected PropertyDescriptor createMultiProperty() {
        return new EnumeratedMultiProperty<>("testEnumerations", "Test enumerations with complex types", KEYS,
                                             VALUES, new int[] {0, 1}, 1.0f);
    }

    @Override
    protected PropertyDescriptor createProperty() {
        return new EnumeratedProperty<>("testEnumerations", "Test enumerations with complex types", KEYS, VALUES, 0,
                                        1.0f);
    }


    @Override
    protected PropertyDescriptor createBadProperty() {

        return new EnumeratedProperty<>("testEnumerations", "Test enumerations with complex types", new String[0],
                                        VALUES, -1, 1.0f);
    }

    @Override
    protected PropertyDescriptor createBadMultiProperty() {
        return new EnumeratedMultiProperty<>("testEnumerations", "Test enumerations with complex types", KEYS,
                                             new Object[0], new int[] {99}, 1.0f);
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
