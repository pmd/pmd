/**
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
        assertEquals(DataType.AutoNumber, DataType.fromString("AutoNumber"));
        assertEquals(DataType.AutoNumber, DataType.fromString("autonumber"));
        assertEquals(DataType.Unknown, DataType.fromString(""));
        assertEquals(DataType.Unknown, DataType.fromString(null));
    }

    @Test
    void testFromTypeName() {
        assertEquals(DataType.Checkbox, DataType.fromTypeName("Boolean"));
        assertEquals(DataType.Currency, DataType.fromTypeName("Currency"));
        assertEquals(DataType.DateTime, DataType.fromTypeName("Datetime"));
        assertEquals(DataType.Number, DataType.fromTypeName("DECIMAL"));
        assertEquals(DataType.Number, DataType.fromTypeName("double"));
        assertEquals(DataType.Text, DataType.fromTypeName("string"));
        assertEquals(DataType.Unknown, DataType.fromTypeName("Object"));
        assertEquals(DataType.Unknown, DataType.fromTypeName(null));
    }

    @Test
    void testRequiresEncoding() {
        assertFalse(DataType.AutoNumber.requiresEscaping);
        assertTrue(DataType.Text.requiresEscaping);
    }
}
