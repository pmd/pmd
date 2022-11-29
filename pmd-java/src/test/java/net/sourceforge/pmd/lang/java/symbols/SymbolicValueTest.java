/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymArray;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

public class SymbolicValueTest {

    static final TypeSystem TS = JavaParsingHelper.TEST_TYPE_SYSTEM;
    
    @Test
    public void testSymValueEquality() {
        Assert.assertEquals("Array of strings",
                            symValueOf(new String[] {"ddd", "eee"}),
                            ofArray(symValueOf("ddd"), symValueOf("eee")));

        Assert.assertEquals("Array of booleans",
                            symValueOf(new boolean[] {true}),
                            symValueOf(new boolean[] {true}));

        Assert.assertNotEquals("Array of booleans",
                               symValueOf(new boolean[] {true}),
                               symValueOf(new boolean[] {false}));

        Assert.assertTrue("valueEquals for int[]",
                          symValueOf(new int[] {10, 11}).valueEquals(new int[] {10, 11}));

        Assert.assertTrue("valueEquals for boolean[]",
                          symValueOf(new boolean[] {false}).valueEquals(new boolean[] {false}));

        Assert.assertFalse("valueEquals for int[]",
                           symValueOf(new int[] {10, 11}).valueEquals(new int[] {10}));

        Assert.assertFalse("valueEquals for double[] 2",
                           symValueOf(new int[] {10, 11}).valueEquals(new double[] {10, 11}));

        Assert.assertFalse("valueEquals for empty arrays",
                           symValueOf(new int[] {}).valueEquals(new double[] {}));

        Assert.assertTrue("valueEquals for empty arrays",
                          symValueOf(new double[] {}).valueEquals(new double[] {}));

    }

    // test only
    static SymbolicValue symValueOf(Object o) {
        return SymbolicValue.of(TS, o);
    }
    
    static SymbolicValue ofArray(SymbolicValue... values) {
        return SymArray.forElements(Arrays.asList(values.clone()));
    }
}
