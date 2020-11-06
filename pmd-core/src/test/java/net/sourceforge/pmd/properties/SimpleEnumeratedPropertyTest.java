/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assume;
import org.junit.Test;

import net.sourceforge.pmd.properties.SimpleEnumeratedPropertyTest.Foo;

/**
 * Evaluates the functionality of the EnumeratedProperty descriptor by testing
 * its ability to catch creation errors (illegal args), flag invalid selections,
 * and serialize/deserialize selection options.
 *
 * @author Brian Remedios
 */
@Deprecated
public class SimpleEnumeratedPropertyTest extends AbstractPropertyDescriptorTester<Foo> {

    private static final String[] KEYS = {"bar", "na", "bee", "coo"};
    private static final Foo[] VALUES = {Foo.BAR, Foo.NA, Foo.BEE, Foo.COO};
    private static final Map<String, Foo> MAPPINGS;


    static {
        Map<String, Foo> map = new HashMap<>();
        map.put("bar", Foo.BAR);
        map.put("na", Foo.NA);
        map.put("bee", Foo.BEE);
        map.put("coo", Foo.COO);
        MAPPINGS = Collections.unmodifiableMap(map);
    }


    public SimpleEnumeratedPropertyTest() {
        super("Enum");
    }


    @Test
    public void testMappings() {
        EnumeratedPropertyDescriptor<Foo, Foo> prop
            = (EnumeratedPropertyDescriptor<Foo, Foo>) createProperty();
        EnumeratedPropertyDescriptor<Foo, List<Foo>> multi
            = (EnumeratedPropertyDescriptor<Foo, List<Foo>>) createMultiProperty();

        assertEquals(MAPPINGS, prop.mappings());
        assertEquals(MAPPINGS, multi.mappings());
    }


    @Override
    protected PropertyDescriptor<Foo> createProperty() {
        return new EnumeratedProperty<>("testEnumerations",
                                        "Test enumerations with complex types",
                                        KEYS,
                                        VALUES, 0, Foo.class,
                                        1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Foo>> createMultiProperty() {
        return new EnumeratedMultiProperty<>("testEnumerations",
                                             "Test enumerations with complex types",
                                             KEYS,
                                             VALUES,
                                             new int[] {0, 1}, Foo.class, 1.0f);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testDefaultIndexOutOfBounds() {
        new EnumeratedMultiProperty<>("testEnumerations", "Test enumerations with simple type",
                                                KEYS, VALUES, new int[] {99}, Foo.class, 1.0f);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNoMappingForDefault() {
        new EnumeratedMultiProperty<>("testEnumerations", "Test enumerations with simple type",
                                      MAPPINGS, Collections.singletonList(Foo.IGNORED), Foo.class, 1.0f);

    }


    @Test
    public void creationTest() {
        PropertyDescriptor<Foo> prop = createProperty();
        PropertyDescriptor<List<Foo>> multi = createMultiProperty();

        for (Map.Entry<String, Foo> e : MAPPINGS.entrySet()) {
            assertEquals(e.getValue(), prop.valueFrom(e.getKey()));
            assertTrue(multi.valueFrom(e.getKey()).contains(e.getValue()));
        }
    }


    @Override
    protected Foo createValue() {
        return randomChoice(VALUES);
    }


    @Override
    protected Foo createBadValue() {
        return Foo.IGNORED; // not in the set of values
    }


    @Override
    protected PropertyDescriptor<Foo> createBadProperty() {
        return new EnumeratedProperty<>("testEnumerations", "Test enumerations with simple type",
                                        new String[0], VALUES, -1, Foo.class, 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Foo>> createBadMultiProperty() {
        return new EnumeratedMultiProperty<>("testEnumerations", "Test enumerations with simple type",
                                             KEYS, VALUES, new int[] {99}, Foo.class, 1.0f);
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
        BAR, NA, BEE, COO, IGNORED
    }
}
