/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import apex.jorje.semantic.symbol.type.BasicType;

public class ExpressionTypeTest {
    @Test
    public void testFromString() {
        assertEquals(ExpressionType.AutoNumber, ExpressionType.fromString("AutoNumber"));
        assertEquals(ExpressionType.AutoNumber, ExpressionType.fromString("autonumber"));
        assertEquals(ExpressionType.Unknown, ExpressionType.fromString(""));
        assertEquals(ExpressionType.Unknown, ExpressionType.fromString(null));
    }

    @Test
    public void testFromBasicType() {
        assertEquals(ExpressionType.Checkbox, ExpressionType.fromBasicType(BasicType.BOOLEAN));
        assertEquals(ExpressionType.Number, ExpressionType.fromBasicType(BasicType.DECIMAL));
        assertEquals(ExpressionType.Number, ExpressionType.fromBasicType(BasicType.DOUBLE));
        assertEquals(ExpressionType.Unknown, ExpressionType.fromBasicType(BasicType.APEX_OBJECT));
        assertEquals(ExpressionType.Unknown, ExpressionType.fromBasicType(null));
    }

    @Test
    public void testRequiresEncoding() {
        assertFalse(ExpressionType.AutoNumber.requiresEscaping);
        assertTrue(ExpressionType.Text.requiresEscaping);
    }
}
