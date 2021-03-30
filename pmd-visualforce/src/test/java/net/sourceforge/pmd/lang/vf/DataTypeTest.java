/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import apex.jorje.semantic.symbol.type.BasicType;

public class DataTypeTest {
    @Test
    public void testFromString() {
        assertEquals(DataType.AutoNumber, DataType.fromString("AutoNumber"));
        assertEquals(DataType.AutoNumber, DataType.fromString("autonumber"));
        assertEquals(DataType.Unknown, DataType.fromString(""));
        assertEquals(DataType.Unknown, DataType.fromString(null));
    }

    @Test
    public void testFromBasicType() {
        assertEquals(DataType.Checkbox, DataType.fromBasicType(BasicType.BOOLEAN));
        assertEquals(DataType.Number, DataType.fromBasicType(BasicType.DECIMAL));
        assertEquals(DataType.Number, DataType.fromBasicType(BasicType.DOUBLE));
        assertEquals(DataType.Unknown, DataType.fromBasicType(BasicType.APEX_OBJECT));
        assertEquals(DataType.Unknown, DataType.fromBasicType(null));
    }

    @Test
    public void testRequiresEncoding() {
        assertFalse(DataType.AutoNumber.requiresEscaping);
        assertTrue(DataType.Text.requiresEscaping);
    }
}
