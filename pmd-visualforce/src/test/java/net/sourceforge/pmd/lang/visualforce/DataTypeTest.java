/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.visualforce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DataTypeTest {

    @Test
    void testFromString() {
        assertEquals(DataType.AUTO_NUMBER, DataType.fromString("AutoNumber"));
        assertEquals(DataType.AUTO_NUMBER, DataType.fromString("autonumber"));
        assertEquals(DataType.UNKNOWN, DataType.fromString(""));
        assertEquals(DataType.UNKNOWN, DataType.fromString(null));
    }

    @Test
    void testFromTypeName() {
        assertEquals(DataType.CHECKBOX, DataType.fromTypeName("Boolean"));
        assertEquals(DataType.CURRENCY, DataType.fromTypeName("Currency"));
        assertEquals(DataType.DATE_TIME, DataType.fromTypeName("Datetime"));
        assertEquals(DataType.NUMBER, DataType.fromTypeName("DECIMAL"));
        assertEquals(DataType.NUMBER, DataType.fromTypeName("double"));
        assertEquals(DataType.TEXT, DataType.fromTypeName("string"));
        assertEquals(DataType.UNKNOWN, DataType.fromTypeName("Object"));
        assertEquals(DataType.UNKNOWN, DataType.fromTypeName(null));
    }

    @Test
    void testRequiresEncoding() {
        assertFalse(DataType.AUTO_NUMBER.requiresEscaping);
        assertTrue(DataType.TEXT.requiresEscaping);
    }
}
