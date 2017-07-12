/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.LongMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.LongProperty;

/**
 * @author Clément Fournier
 */
public class LongPropertyTest extends AbstractPropertyDescriptorTester<Long> {

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
        return randomBool() ? randomLong(MIN - SHIFT, MIN) : randomLong(MAX, MAX + SHIFT);
    }


    @Override
    protected PropertyDescriptor<Long> createProperty() {
        return new LongProperty("testFloat", "Test float property", MIN, MAX, 90L, 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Long>> createMultiProperty() {
        return new LongMultiProperty("testFloat", "Test float property", MIN, MAX,
                                     new Long[] {-1000L, 0L, 100L, 20L}, 1.0f);
    }


    @Override
    protected PropertyDescriptor<Long> createBadProperty() {
        return new LongProperty("testFloat", "Test float property", 200L, -400L, 900L, 1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Long>> createBadMultiProperty() {
        return new LongMultiProperty("testFloat", "Test float property", 0L, 5L,
                                     new Long[] {-1000L, 0L, 100L, 20L}, 1.0f);
    }


    public static LongProperty randomProperty(int nameLength, int descLength, boolean multiValue) {

        long defalt = randomLong(0, 1000);

        return new LongProperty(randomString(nameLength), randomString(descLength), defalt - 10000, defalt + 10000,
                                defalt, 0f);
    }

}
