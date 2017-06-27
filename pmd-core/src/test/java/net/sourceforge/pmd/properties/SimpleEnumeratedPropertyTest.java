/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;

import org.junit.Assume;
import org.junit.Test;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
import net.sourceforge.pmd.properties.SimpleEnumeratedPropertyTest.Foo;

/**
 * Evaluates the functionality of the EnumeratedProperty descriptor by testing
 * its ability to catch creation errors (illegal args), flag invalid selections,
 * and serialize/deserialize selection options.
 *
 * @author Brian Remedios
 */
public class SimpleEnumeratedPropertyTest extends AbstractPropertyDescriptorTester<Foo> {

    private static final String[] KEYS = {"bar", "na", "bee", "coo",};
    private static final Foo[] VALUES = Foo.values();


    public SimpleEnumeratedPropertyTest() {
        super("Enum");
    }


    @Override
    protected Foo createValue() {
        return randomChoice(Foo.values());
    }


    @Override
    protected Foo createBadValue() {
        return null; // not in the set of values
    }


    @Override
    protected PropertyDescriptor<List<Foo>> createMultiProperty() {
        return new EnumeratedMultiProperty<>("testEnumerations",
                                             "Test enumerations with complex types",
                                             KEYS,
                                             VALUES,
                                             new int[] {0, 1}, 1.0f);
    }


    @Override
    protected PropertyDescriptor<Foo> createProperty() {
        return new EnumeratedProperty<>("testEnumerations",
                                        "Test enumerations with complex types",
                                        KEYS,
                                        VALUES, 0,
                                        1.0f);
    }


    @Override
    protected PropertyDescriptor<Foo> createBadProperty() {
        return new EnumeratedProperty<>("testEnumerations", "Test enumerations with simple type",
                                        new String[0], VALUES, -1, 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Foo>> createBadMultiProperty() {
        return new EnumeratedMultiProperty<>("testEnumerations", "Test enumerations with simple type",
                                             KEYS, VALUES, new int[] {99}, 1.0f);
    }


    @Override
    @Test
    public void testFactorySingleValue() {
        Assume.assumeTrue("The EnumeratedProperty factory is not implemented yet", false);
    }


    @Override
    @Test
    public void testFactoryMultiValueCustomDelimiter() {
        Assume.assumeTrue("The EnumeratedProperty factory is not implemented yet", false);
    }


    @Override
    @Test
    public void testFactoryMultiValueDefaultDelimiter() {
        Assume.assumeTrue("The EnumeratedProperty factory is not implemented yet", false);
    }


    enum Foo {
        BAR, NA, BEE, COO
    }
}
