/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;

/**
 * Evaluates the functionality of the DoubleProperty descriptor by testing its
 * ability to catch creation errors (illegal args), flag out-of-range test
 * values, and serialize/deserialize groups of double values onto/from a string
 * buffer.
 *
 * @author Brian Remedios
 */
@Deprecated
public class DoublePropertyTest extends AbstractNumericPropertyDescriptorTester<Double> {

    private static final double MIN = -10.0;
    private static final double MAX = 100.0;
    private static final double SHIFT = 5.0;


    public DoublePropertyTest() {
        super("Double");
    }


    @Override
    protected Double createValue() {
        return randomDouble(MIN, MAX);
    }


    @Override
    protected Double createBadValue() {
        return randomBool() ? randomDouble(MIN - SHIFT, MIN - 0.01) : randomDouble(MAX + 0.01, MAX + SHIFT);

    }


    protected DoubleProperty.DoublePBuilder singleBuilder() {
        return DoubleProperty.named("test").desc("foo")
                             .range(MIN, MAX).defaultValue(createValue()).uiOrder(1.0f);
    }


    protected DoubleMultiProperty.DoubleMultiPBuilder multiBuilder() {
        return DoubleMultiProperty.named("test").desc("foo")
                                  .range(MIN, MAX).defaultValues(createValue(), createValue()).uiOrder(1.0f);
    }


    @Override
    protected PropertyDescriptor<Double> createProperty() {
        return new DoubleProperty("testDouble", "Test double property", MIN, MAX, 9.0, 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Double>> createMultiProperty() {
        return new DoubleMultiProperty("testDouble", "Test double property", MIN, MAX,
                                       new Double[] {-1d, 0d, 1d, 2d}, 1.0f);
    }


    @Override
    protected PropertyDescriptor<Double> createBadProperty() {
        return new DoubleProperty("testDouble", "Test double property", MAX, MIN, 9.0, 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Double>> createBadMultiProperty() {
        return new DoubleMultiProperty("testDouble", "Test double property", MIN, MAX,
                                       new Double[] {MIN - SHIFT, MIN, MIN + SHIFT, MAX + SHIFT}, 1.0f);
    }


    @Override
    protected Double min() {
        return MIN;
    }


    @Override
    protected Double max() {
        return MAX;
    }
}
