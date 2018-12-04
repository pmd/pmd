/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;


/**
 * Evaluates the functionality of the IntegerProperty descriptor by testing its
 * ability to catch creation errors (illegal args), flag out-of-range test
 * values, and serialize/deserialize groups of integers onto/from a string
 * buffer.
 *
 * @author Brian Remedios
 */
@Deprecated
public class IntegerPropertyTest extends AbstractNumericPropertyDescriptorTester<Integer> {

    private static final int MIN = 1;
    private static final int MAX = 12;
    private static final int SHIFT = 4;


    public IntegerPropertyTest() {
        super("Integer");
    }


    /*   @Override
       @Test
       public void testErrorForBadSingle() {
       } // not until int properties get ranges

       @Override
       @Test
       public void testErrorForBadMulti() {
       } // not until int properties get ranges

   */
    @Override
    protected Integer createValue() {
        return randomInt(MIN, MAX);
    }


    @Override
    protected Integer createBadValue() {
        return randomBool() ? randomInt(MIN - SHIFT, MIN - 1) : randomInt(MAX + 1, MAX + SHIFT);
    }


    protected IntegerProperty.IntegerPBuilder singleBuilder() {
        return IntegerProperty.named("test").desc("foo")
                              .range(MIN, MAX).defaultValue(createValue()).uiOrder(1.0f);
    }


    protected IntegerMultiProperty.IntegerMultiPBuilder multiBuilder() {
        return IntegerMultiProperty.named("test").desc("foo")
                                   .range(MIN, MAX).defaultValues(createValue(), createValue()).uiOrder(1.0f);
    }


    @Override
    protected PropertyDescriptor<Integer> createProperty() {
        return new IntegerProperty("testInteger", "Test integer property", MIN, MAX, MAX - 1, 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Integer>> createMultiProperty() {
        return new IntegerMultiProperty("testInteger", "Test integer property", MIN, MAX,
                                        new Integer[] {MIN, MIN + 1, MAX - 1, MAX}, 1.0f);
    }


    @Override
    protected PropertyDescriptor<Integer> createBadProperty() {
        return new IntegerProperty("", "Test integer property", MIN, MAX, MAX + 1, 1.0f);

    }


    @Override
    protected PropertyDescriptor<List<Integer>> createBadMultiProperty() {
        return new IntegerMultiProperty("testInteger", "", MIN, MAX, new Integer[] {MIN - 1, MAX}, 1.0f);
    }


    @Override
    protected Integer min() {
        return MIN;
    }


    @Override
    protected Integer max() {
        return MAX;
    }
}
