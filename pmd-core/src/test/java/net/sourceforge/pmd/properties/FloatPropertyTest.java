/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;

/**
 * Evaluates the functionality of the FloatProperty descriptor by testing its
 * ability to catch creation errors (illegal args), flag out-of-range test
 * values, and serialize/deserialize groups of float values onto/from a string
 * buffer.
 *
 * @author Brian Remedios
 */
public class FloatPropertyTest extends AbstractNumericPropertyDescriptorTester<Float> {

    private static final float MIN = 1.0f;
    private static final float MAX = 11.0f;
    private static final float SHIFT = 3.0f;


    public FloatPropertyTest() {
        super("Float");
    }


    @Override
    protected Float createValue() {
        return randomFloat(MIN, MAX);
    }


    @Override
    protected Float createBadValue() {
        return randomBool() ? randomFloat(MIN - SHIFT, MIN) : randomFloat(MAX, MAX + SHIFT);
    }


    @Override
    protected PropertyDescriptor<Float> createProperty() {
        return new FloatProperty("testFloat", "Test float property", MIN, MAX, 9.0f, 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Float>> createMultiProperty() {
        return new FloatMultiProperty("testFloat", "Test float property", MIN, MAX,
                                      new Float[] {-1f, 0f, 1f, 2f}, 1.0f);
    }


    @Override
    protected PropertyDescriptor<Float> createBadProperty() {
        return new FloatProperty("testFloat", "Test float property", 5f, 4f, 9.0f, 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Float>> createBadMultiProperty() {
        return new FloatMultiProperty("testFloat", "Test float property", 0f, 5f,
                                      new Float[] {-1f, 0f, 1f, 2f}, 1.0f);
    }


    public static FloatProperty randomProperty(int nameLength, int descLength, boolean multiValue) {

        float defalt = randomFloat(0, 1000f);

        return new FloatProperty(randomString(nameLength), randomString(descLength), defalt - 1000f, defalt + 1000,
                                 defalt, 0f);
    }
}
