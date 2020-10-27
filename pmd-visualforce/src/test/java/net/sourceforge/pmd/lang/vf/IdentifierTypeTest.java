/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import apex.jorje.semantic.symbol.type.BasicType;

public class IdentifierTypeTest {
    @Test
    public void testFromString() {
        assertEquals(IdentifierType.AutoNumber, IdentifierType.fromString("AutoNumber"));
        assertEquals(IdentifierType.AutoNumber, IdentifierType.fromString("autonumber"));
        assertEquals(IdentifierType.Unknown, IdentifierType.fromString(""));
        assertEquals(IdentifierType.Unknown, IdentifierType.fromString(null));
    }

    @Test
    public void testFromBasicType() {
        assertEquals(IdentifierType.Checkbox, IdentifierType.fromBasicType(BasicType.BOOLEAN));
        assertEquals(IdentifierType.Number, IdentifierType.fromBasicType(BasicType.DECIMAL));
        assertEquals(IdentifierType.Number, IdentifierType.fromBasicType(BasicType.DOUBLE));
        assertEquals(IdentifierType.Unknown, IdentifierType.fromBasicType(BasicType.APEX_OBJECT));
        assertEquals(IdentifierType.Unknown, IdentifierType.fromBasicType(null));
    }

    @Test
    public void testRequiresEncoding() {
        assertFalse(IdentifierType.AutoNumber.requiresEscaping);
        assertTrue(IdentifierType.Text.requiresEscaping);
    }
}
