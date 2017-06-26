/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.IntegerMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

/**
 * Evaluates the functionality of the IntegerProperty descriptor by testing its
 * ability to catch creation errors (illegal args), flag out-of-range test
 * values, and serialize/deserialize groups of integers onto/from a string
 * buffer.
 *
 * @author Brian Remedios
 */
public class IntegerPropertyTest extends AbstractPropertyDescriptorTester<Integer> {

    private static final int MIN = 1;
    private static final int MAX = 12;
    private static final int SHIFT = 3;

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
        return randomBool() ? randomInt(MIN - SHIFT, MIN) : randomInt(MAX, MAX + SHIFT);
    }

    @Override
    protected PropertyDescriptor<Integer> createProperty() {
        return new IntegerProperty("testInteger", "Test integer property", MIN, MAX, MAX - 1, 1.0f);
    }

    @Override
    protected PropertyDescriptor<List<Integer>> createMultiProperty() {
        return new IntegerMultiProperty("testInteger", "Test integer property", MIN, MAX,
                                        new Integer[] { MIN, MIN + 1, MAX - 1, MAX }, 1.0f);
    }

    @Override
    protected PropertyDescriptor<Integer> createBadProperty() {
        return new IntegerProperty("", "Test integer property", MIN, MAX, MAX + 1, 1.0f);

    }

    @Override
    protected PropertyDescriptor<List<Integer>> createBadMultiProperty() {
        return new IntegerMultiProperty("testInteger", "", MIN, MAX, new Integer[] { MIN - 1, MAX }, 1.0f) ;
    }
}
