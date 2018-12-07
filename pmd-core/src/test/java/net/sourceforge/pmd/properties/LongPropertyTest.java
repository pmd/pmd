/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;


/**
 * @author Clément Fournier
 */
@Deprecated
public class LongPropertyTest extends AbstractNumericPropertyDescriptorTester<Long> {

    private static final long MIN = 10L;
    private static final long MAX = 11000L;
    private static final long SHIFT = 300L;


    public LongPropertyTest() {
        super("Long");
    }


    @Override
    protected Long createValue() {
        return randomLong(MIN, MAX);
    }


    @Override
    protected Long createBadValue() {
        return randomBool() ? randomLong(MIN - SHIFT, MIN) : randomLong(MAX + 1, MAX + SHIFT);
    }


    @Override
    protected LongProperty.LongPBuilder singleBuilder() {
        return LongProperty.named("test").desc("foo")
                           .range(MIN, MAX).defaultValue(createValue()).uiOrder(1.0f);
    }


    @Override
    protected LongMultiProperty.LongMultiPBuilder multiBuilder() {
        return LongMultiProperty.named("test").desc("foo")
                                .range(MIN, MAX).defaultValues(createValue(), createValue()).uiOrder(1.0f);
    }


    @Override
    protected PropertyDescriptor<Long> createProperty() {
        return new LongProperty("testFloat", "Test float property", MIN, MAX, 90L, 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Long>> createMultiProperty() {
        return new LongMultiProperty("testFloat", "Test float property", MIN, MAX,
            new Long[]{1000L, 10L, 100L, 20L}, 1.0f);
    }


    @Override
    protected PropertyDescriptor<Long> createBadProperty() {
        return new LongProperty("testFloat", "Test float property", 200L, -400L, 900L, 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Long>> createBadMultiProperty() {
        return new LongMultiProperty("testFloat", "Test float property", 0L, 5L,
            new Long[]{-1000L, 0L, 100L, 20L}, 1.0f);
    }


    @Override
    protected Long min() {
        return MIN;
    }


    @Override
    protected Long max() {
        return MAX;
    }
}
