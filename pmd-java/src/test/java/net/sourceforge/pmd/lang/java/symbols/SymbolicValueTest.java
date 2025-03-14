/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymArray;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

class SymbolicValueTest {

    static final TypeSystem TS = JavaParsingHelper.TEST_TYPE_SYSTEM;

    @Test
    void testSymValueEquality() {
        assertEquals(symValueOf(new String[] {"ddd", "eee"}),
                     ofArray(symValueOf("ddd"), symValueOf("eee")),
                     "Array of strings");

        assertEquals(symValueOf(new boolean[] {true}),
                     symValueOf(new boolean[] {true}),
                     "Array of booleans");

        assertNotEquals(symValueOf(new boolean[] {true}),
                        symValueOf(new boolean[] {false}),
                        "Array of booleans");

        assertTrue(symValueOf(new int[] {10, 11}).valueEquals(new int[] {10, 11}),
                   "valueEquals for int[]");

        assertTrue(symValueOf(new boolean[] {false}).valueEquals(new boolean[] {false}),
                   "valueEquals for boolean[]");

        assertFalse(symValueOf(new int[] {10, 11}).valueEquals(new int[] {10}),
                    "valueEquals for int[]");

        assertFalse(symValueOf(new int[] {10, 11}).valueEquals(new double[] {10, 11}),
                    "valueEquals for double[] 2");

        assertFalse(symValueOf(new int[] {}).valueEquals(new double[] {}),
                    "valueEquals for empty arrays");

        assertTrue(symValueOf(new double[] {}).valueEquals(new double[] {}),
                   "valueEquals for empty arrays");
    }

    // test only
    static SymbolicValue symValueOf(Object o) {
        return SymbolicValue.of(TS, o);
    }
    
    static SymbolicValue ofArray(SymbolicValue... values) {
        return SymArray.forElements(Arrays.asList(values.clone()));
    }
}
